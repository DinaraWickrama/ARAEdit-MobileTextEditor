package com.example.mobiletexteditor.ui

import android.app.Application
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiletexteditor.autosave.AutosaveManager
import com.example.mobiletexteditor.data.AppDatabase
import com.example.mobiletexteditor.data.FileEntity
import com.example.mobiletexteditor.data.VersionEntity
import com.example.mobiletexteditor.editor.FileManager
import com.example.mobiletexteditor.editor.UndoRedoManager
import com.example.mobiletexteditor.highlighting.KotlinHighlighter
import com.example.mobiletexteditor.highlighting.MarkdownHighlighter
import com.example.mobiletexteditor.versioncontrol.DeltaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FileType { KOTLIN, MARKDOWN, HTML, CSS, PLAIN }

/** Extensions the user can pick when saving a new file, per the assignment's
 *  requirement to support more than plain text (.kt, .html, .css, etc.). */
enum class FileTypeOption(val extension: String, val label: String) {
    KOTLIN(".kt", "Kotlin (.kt)"),
    MARKDOWN(".md", "Markdown (.md)"),
    HTML(".html", "HTML (.html)"),
    CSS(".css", "CSS (.css)"),
    PLAIN(".txt", "Plain text (.txt)")
}

fun FileTypeOption.toFileType(): FileType = when (this) {
    FileTypeOption.KOTLIN -> FileType.KOTLIN
    FileTypeOption.MARKDOWN -> FileType.MARKDOWN
    FileTypeOption.HTML -> FileType.HTML
    FileTypeOption.CSS -> FileType.CSS
    FileTypeOption.PLAIN -> FileType.PLAIN
}

fun FileType.toFileTypeOption(): FileTypeOption = when (this) {
    FileType.KOTLIN -> FileTypeOption.KOTLIN
    FileType.MARKDOWN -> FileTypeOption.MARKDOWN
    FileType.HTML -> FileTypeOption.HTML
    FileType.CSS -> FileTypeOption.CSS
    FileType.PLAIN -> FileTypeOption.PLAIN
}

data class EditorUiState(
    val bufferText: String = "",
    val displayName: String = "untitled",
    val isReadOnly: Boolean = false,
    val wordWrapEnabled: Boolean = true,
    val fileType: FileType = FileType.PLAIN,
    val recoveryAvailable: Boolean = false,
    val versionHistory: List<VersionEntity> = emptyList(),
    val hasActiveFile: Boolean = false,
    val errorMessage: String? = null,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val matchCount: Int = 0,
    val currentMatchIndex: Int = -1
)

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val fileManager = FileManager(application)
    private val undoRedo = UndoRedoManager()
    private val autosave = AutosaveManager(application)
    private val deltaManager = DeltaManager(db.versionDao(), db.fileDao())

    private var activeUri: Uri? = null
    private var activeFileId: Long? = null

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    val recentFiles = db.fileDao().recentFiles()

    init {
        _uiState.value = _uiState.value.copy(recoveryAvailable = autosave.hasRecoverableSession())
        startAutosave()
    }

    private fun startAutosave() {
        autosave.start(viewModelScope, activeFileId) { _uiState.value.bufferText }
    }

    fun restoreFromCrash() {
        val recovered = autosave.readRecoveredText()
        undoRedo.initialize(recovered)
        _uiState.value = _uiState.value.copy(bufferText = recovered, recoveryAvailable = false)
    }

    fun dismissRecovery() {
        autosave.clearRecoverySession()
        _uiState.value = _uiState.value.copy(recoveryAvailable = false)
    }

    fun newFile(fileType: FileType = FileType.PLAIN) {
        activeUri = null
        activeFileId = null
        undoRedo.initialize("")
        val suggestedName = when (fileType) {
            FileType.KOTLIN -> "untitled.kt"
            FileType.MARKDOWN -> "untitled.md"
            FileType.HTML -> "untitled.html"
            FileType.CSS -> "untitled.css"
            FileType.PLAIN -> "untitled"
        }
        _uiState.value = EditorUiState(
            bufferText = "",
            displayName = suggestedName,
            fileType = fileType,
            hasActiveFile = false
        )
    }

    /** Opens a file the user picked via the system file browser (may or may not be tracked yet). */
    fun openFile(uri: Uri) {
        viewModelScope.launch {
            try {
                fileManager.takePersistablePermission(uri)
                val text = fileManager.readText(uri)
                val name = fileManager.displayNameOf(uri)
                activeUri = uri

                val existing = db.fileDao().findByUri(uri.toString())
                val fileId: Long
                if (existing != null) {
                    fileId = existing.fileId
                    db.fileDao().touch(fileId)
                } else {
                    fileId = db.fileDao().insert(FileEntity(displayName = name, uriString = uri.toString()))
                    deltaManager.createBaseVersion(fileId, text)
                }
                activeFileId = fileId

                undoRedo.initialize(text)
                _uiState.value = _uiState.value.copy(
                    bufferText = text,
                    displayName = name,
                    fileType = inferFileType(name),
                    versionHistory = deltaManager.history(fileId),
                    hasActiveFile = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Could not open file: ${e.message}")
            }
        }
    }

    /** Opens a file already tracked in Recent Files, by its database record. */
    fun openRecentFile(file: FileEntity) {
        viewModelScope.launch {
            try {
                val uri = Uri.parse(file.uriString)
                val text = fileManager.readText(uri)
                activeUri = uri
                activeFileId = file.fileId
                db.fileDao().touch(file.fileId)
                undoRedo.initialize(text)
                _uiState.value = _uiState.value.copy(
                    bufferText = text,
                    displayName = file.displayName,
                    fileType = inferFileType(file.displayName),
                    isReadOnly = file.isReadOnly,
                    versionHistory = deltaManager.history(file.fileId),
                    hasActiveFile = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Could not open \"${file.displayName}\": ${e.message}"
                )
            }
        }
    }

    fun deleteRecentFile(file: FileEntity) {
        viewModelScope.launch {
            db.fileDao().delete(file)
            if (activeFileId == file.fileId) {
                newFile()
            }
        }
    }

    fun onTextChanged(newText: String) {
        if (_uiState.value.isReadOnly) return
        val oldText = _uiState.value.bufferText
        val insertedChar = singleCharacterInserted(oldText, newText)
        val isMidWordTyping = insertedChar != null && (insertedChar.isLetterOrDigit() || insertedChar == '_')

        if (isMidWordTyping) {
            // Still typing the same word — keep the buffer in sync, but don't
            // create a new undo checkpoint until the word actually finishes.
            undoRedo.updateLiveText(newText)
        } else {
            // A word just finished (space/punctuation), or this was a deletion
            // or paste — commit a proper undo checkpoint.
            undoRedo.recordEdit(newText)
        }
        _uiState.value = _uiState.value.copy(bufferText = newText)
    }

    /** Returns the single character that was inserted if newText is exactly
     *  oldText with one character added somewhere, otherwise null (covers
     *  deletions, pastes, and multi-character changes). */
    private fun singleCharacterInserted(oldText: String, newText: String): Char? {
        if (newText.length != oldText.length + 1) return null
        var i = 0
        while (i < oldText.length && i < newText.length && oldText[i] == newText[i]) i++
        if (newText.substring(i + 1) != oldText.substring(i)) return null
        return newText[i]
    }

    fun undo() {
        _uiState.value = _uiState.value.copy(bufferText = undoRedo.undo())
    }

    fun redo() {
        _uiState.value = _uiState.value.copy(bufferText = undoRedo.redo())
    }

    fun toggleWordWrap() {
        _uiState.value = _uiState.value.copy(wordWrapEnabled = !_uiState.value.wordWrapEnabled)
    }

    fun setReadOnly(readOnly: Boolean) {
        val fileId = activeFileId ?: return
        viewModelScope.launch {
            db.fileDao().setReadOnly(fileId, readOnly)
            _uiState.value = _uiState.value.copy(isReadOnly = readOnly)
        }
    }

    /** Save = write to disk (SAF) AND record a new delta version (no duplication). */
    fun save(label: String? = null) {
        val uri = activeUri ?: return
        val fileId = activeFileId ?: return
        viewModelScope.launch {
            try {
                fileManager.writeText(uri, _uiState.value.bufferText)
                deltaManager.saveNewVersion(fileId, _uiState.value.bufferText, label)
                _uiState.value = _uiState.value.copy(
                    versionHistory = deltaManager.history(fileId),
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Save failed: ${e.message}")
            }
        }
    }

    fun saveAs(newUri: Uri) {
        viewModelScope.launch {
            try {
                fileManager.writeText(newUri, _uiState.value.bufferText)

                try {
                    fileManager.takePersistablePermission(newUri)
                } catch (permissionError: Exception) {
                    // Ignored on purpose — the write above already succeeded.
                }

                activeUri = newUri
                val name = fileManager.displayNameOf(newUri)

                val existing = db.fileDao().findByUri(newUri.toString())
                val fileId: Long
                if (existing != null) {
                    fileId = existing.fileId
                    db.fileDao().touch(fileId)
                } else {
                    fileId = db.fileDao().insert(FileEntity(displayName = name, uriString = newUri.toString()))
                    deltaManager.createBaseVersion(fileId, _uiState.value.bufferText)
                }
                activeFileId = fileId

                _uiState.value = _uiState.value.copy(
                    displayName = name,
                    fileType = inferFileType(name),
                    versionHistory = deltaManager.history(fileId),
                    hasActiveFile = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Save As failed: ${e.message}")
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun toggleSearch() {
        _uiState.value = _uiState.value.copy(isSearchVisible = !_uiState.value.isSearchVisible)
    }

    private fun findAllMatches(text: String, query: String): List<IntRange> {
        if (query.isEmpty()) return emptyList()
        val matches = mutableListOf<IntRange>()
        var index = text.indexOf(query)
        while (index != -1) {
            matches.add(index until index + query.length)
            index = text.indexOf(query, index + 1)
        }
        return matches
    }

    fun updateSearchQuery(query: String) {
        val matches = findAllMatches(_uiState.value.bufferText, query)
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            matchCount = matches.size,
            currentMatchIndex = if (matches.isEmpty()) -1 else 0
        )
    }

    fun findNext() {
        val matches = findAllMatches(_uiState.value.bufferText, _uiState.value.searchQuery)
        if (matches.isEmpty()) return
        val next = (_uiState.value.currentMatchIndex + 1) % matches.size
        _uiState.value = _uiState.value.copy(currentMatchIndex = next, matchCount = matches.size)
    }

    fun findPrevious() {
        val matches = findAllMatches(_uiState.value.bufferText, _uiState.value.searchQuery)
        if (matches.isEmpty()) return
        val idx = _uiState.value.currentMatchIndex
        val prev = if (idx <= 0) matches.size - 1 else idx - 1
        _uiState.value = _uiState.value.copy(currentMatchIndex = prev, matchCount = matches.size)
    }

    fun replaceCurrent(replacement: String) {
        val query = _uiState.value.searchQuery
        if (query.isEmpty()) return
        val matches = findAllMatches(_uiState.value.bufferText, query)
        val idx = _uiState.value.currentMatchIndex
        if (idx !in matches.indices) return
        val newText = _uiState.value.bufferText.replaceRange(matches[idx].first, matches[idx].last + 1, replacement)
        onTextChanged(newText)
        val newMatches = findAllMatches(newText, query)
        _uiState.value = _uiState.value.copy(
            matchCount = newMatches.size,
            currentMatchIndex = if (newMatches.isEmpty()) -1 else idx.coerceAtMost(newMatches.size - 1)
        )
    }

    fun replaceAll(replacement: String) {
        val query = _uiState.value.searchQuery
        if (query.isEmpty()) return
        onTextChanged(_uiState.value.bufferText.replace(query, replacement))
        _uiState.value = _uiState.value.copy(matchCount = 0, currentMatchIndex = -1)
    }

    fun rollbackTo(versionNumber: Int) {
        val fileId = activeFileId ?: return
        viewModelScope.launch {
            val restored = deltaManager.rollbackTo(fileId, versionNumber)
            undoRedo.initialize(restored)
            _uiState.value = _uiState.value.copy(
                bufferText = restored,
                versionHistory = deltaManager.history(fileId)
            )
        }
    }

    suspend fun diffBetween(fromVersion: Int, toVersion: Int): List<String> {
        val fileId = activeFileId ?: return emptyList()
        return deltaManager.diffBetween(fileId, fromVersion, toVersion)
    }
    suspend fun structuredDiffBetween(fromVersion: Int, toVersion: Int): List<com.example.mobiletexteditor.versioncontrol.DiffLine> {
        val fileId = activeFileId ?: return emptyList()
        return deltaManager.structuredDiff(fileId, fromVersion, toVersion)
    }

    fun highlightedText(): AnnotatedString {
        val text = _uiState.value.bufferText
        return when (_uiState.value.fileType) {
            FileType.KOTLIN -> KotlinHighlighter.highlight(text)
            FileType.MARKDOWN -> MarkdownHighlighter.highlight(text)
            else -> AnnotatedString(text)
        }
    }

    private fun inferFileType(name: String): FileType = when {
        name.endsWith(".kt") -> FileType.KOTLIN
        name.endsWith(".md") || name.endsWith(".markdown") -> FileType.MARKDOWN
        name.endsWith(".html") || name.endsWith(".htm") -> FileType.HTML
        name.endsWith(".css") -> FileType.CSS
        else -> FileType.PLAIN
    }

    override fun onCleared() {
        autosave.stop()
        super.onCleared()
    }
}