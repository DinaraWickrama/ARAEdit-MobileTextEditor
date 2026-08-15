package com.example.mobiletexteditor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mobiletexteditor.highlighting.KotlinHighlighter
import com.example.mobiletexteditor.highlighting.MarkdownHighlighter
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onSaveAsRequested: (String) -> Unit,
    onShowVersionHistory: () -> Unit,
    onBackToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var overflowMenuOpen by remember { mutableStateOf(false) }
    var showSaveLabelDialog by remember { mutableStateOf(false) }
    var saveLabelInput by remember { mutableStateOf("") }
    var showSaveAsDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    if (uiState.recoveryAvailable) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRecovery() },
            title = { Text("Recover unsaved changes?") },
            text = { Text("It looks like the app closed unexpectedly. Restore the last autosaved buffer?") },
            confirmButton = {
                TextButton(onClick = { viewModel.restoreFromCrash() }) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRecovery() }) { Text("Discard") }
            }
        )
    }

    uiState.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) { Text("OK") }
            }
        )
    }

    if (showSaveLabelDialog) {
        AlertDialog(
            onDismissRequest = { showSaveLabelDialog = false },
            title = { Text("Save version") },
            text = {
                Column {
                    Text("Optionally name this snapshot (leave blank for an unnamed version):")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = saveLabelInput,
                        onValueChange = { saveLabelInput = it },
                        label = { Text("Version name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.save(label = saveLabelInput.trim().ifBlank { null })
                    showSaveLabelDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveLabelDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showSaveAsDialog) {
        SaveAsDialog(
            initialName = uiState.displayName.substringBeforeLast('.', uiState.displayName),
            initialType = uiState.fileType.toFileTypeOption(),
            onConfirm = { fullFileName ->
                showSaveAsDialog = false
                onSaveAsRequested(fullFileName)
            },
            onCancel = { showSaveAsDialog = false }
        )
    }
    LaunchedEffect(Unit) {
        // A small delay or yield ensures the component is fully placed in the layout 
        // before requesting focus, avoiding "FocusRequester is not initialized" errors.
        kotlinx.coroutines.delay(100)
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
            // If focus fails, the user can still tap manually.
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.displayName + if (uiState.isReadOnly) "  (read-only)" else "",
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.undo() }) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }
                    IconButton(onClick = { viewModel.redo() }) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                    }
                    IconButton(onClick = {
                        if (uiState.hasActiveFile) {
                            saveLabelInput = ""
                            showSaveLabelDialog = true
                        } else {
                            showSaveAsDialog = true
                        }
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save")
                    }
                    IconButton(onClick = { overflowMenuOpen = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = overflowMenuOpen, onDismissRequest = { overflowMenuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Save As") },
                            onClick = { overflowMenuOpen = false; showSaveAsDialog = true }
                        )
                        DropdownMenuItem(
                            text = { Text("Version History") },
                            onClick = { overflowMenuOpen = false; onShowVersionHistory() }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { viewModel.setReadOnly(!uiState.isReadOnly) }) {
                        Icon(
                            if (uiState.isReadOnly) Icons.Filled.Lock else Icons.Filled.LockOpen,
                            contentDescription = if (uiState.isReadOnly) "Unlock file" else "Lock file as read-only"
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Word wrap")
                    Spacer(Modifier.width(8.dp))
                    Switch(checked = uiState.wordWrapEnabled, onCheckedChange = { viewModel.toggleWordWrap() })
                }
            }

            if (uiState.isSearchVisible) {
                HorizontalDivider()
                SearchReplaceBar(
                    query = uiState.searchQuery,
                    matchCount = uiState.matchCount,
                    currentMatchIndex = uiState.currentMatchIndex,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onNext = { viewModel.findNext() },
                    onPrevious = { viewModel.findPrevious() },
                    onReplaceOne = { viewModel.replaceCurrent(it) },
                    onReplaceAll = { viewModel.replaceAll(it) },
                    onClose = { viewModel.toggleSearch() }
                )
                HorizontalDivider()
            }

            // Local text field state — preserves cursor position during normal typing.
            var textFieldValue by remember { mutableStateOf(TextFieldValue(uiState.bufferText)) }

            // Only force-reset the cursor when the text changed from OUTSIDE this field
            // (undo/redo, opening a file, restoring a version) — not from normal typing.
            LaunchedEffect(uiState.bufferText) {
                if (uiState.bufferText != textFieldValue.text) {
                    textFieldValue = TextFieldValue(
                        uiState.bufferText,
                        selection = TextRange(uiState.bufferText.length)
                    )
                }
            }

            val fileType = uiState.fileType
            val searchQuery = uiState.searchQuery
            val currentMatchIndex = uiState.currentMatchIndex

            // Combined transformation: syntax highlighting UNDER search-match highlighting.
            val combinedTransformation = remember(fileType, searchQuery, currentMatchIndex) {
                VisualTransformation { original ->
                    val sourceText = original.text
                    val base: AnnotatedString = when (fileType) {
                        FileType.KOTLIN -> KotlinHighlighter.highlight(sourceText)
                        FileType.MARKDOWN -> MarkdownHighlighter.highlight(sourceText)
                        else -> AnnotatedString(sourceText)
                    }
                    val builder = AnnotatedString.Builder(base)
                    if (searchQuery.isNotEmpty()) {
                        var startIndex = sourceText.indexOf(searchQuery)
                        var matchNumber = 0
                        while (startIndex != -1) {
                            val background = if (matchNumber == currentMatchIndex) {
                                Color(0xFFFFA726)
                            } else {
                                Color(0xFFFFF59D)
                            }
                            builder.addStyle(
                                SpanStyle(background = background),
                                startIndex,
                                startIndex + searchQuery.length
                            )
                            startIndex = sourceText.indexOf(searchQuery, startIndex + 1)
                            matchNumber++
                        }
                    }
                    TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
                }
            }

            if (uiState.wordWrapEnabled) {
                // Word wrap ON: text field fills the width, long lines wrap normally.
                Box(modifier = Modifier.fillMaxSize()) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                            viewModel.onTextChanged(newValue.text)
                        },
                        readOnly = uiState.isReadOnly,
                        visualTransformation = combinedTransformation,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSynthesis = androidx.compose.ui.text.font.FontSynthesis.All),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .focusRequester(focusRequester)
                    )
                }
            } else {
                // Word wrap OFF: outer Box scrolls horizontally, inner field has NO width
                // constraint so it can grow as wide as the longest line actually is.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                            viewModel.onTextChanged(newValue.text)
                        },
                        readOnly = uiState.isReadOnly,
                        visualTransformation = combinedTransformation,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSynthesis = androidx.compose.ui.text.font.FontSynthesis.All),
                        modifier = Modifier
                            .padding(8.dp)
                            .focusRequester(focusRequester)
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveAsDialog(
    initialName: String,
    initialType: FileTypeOption,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var nameInput by remember { mutableStateOf(initialName) }
    var selectedType by remember { mutableStateOf(initialType) }
    var typeMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Save As") },
        text = {
            Column {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("File name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("File type", style = MaterialTheme.typography.labelMedium)
                Box {
                    OutlinedButton(
                        onClick = { typeMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedType.label)
                    }
                    DropdownMenu(expanded = typeMenuExpanded, onDismissRequest = { typeMenuExpanded = false }) {
                        FileTypeOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = { selectedType = option; typeMenuExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val cleanName = nameInput.trim().ifBlank { "untitled" }
                onConfirm(cleanName + selectedType.extension)
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    )
}

@Composable
private fun SearchReplaceBar(
    query: String,
    matchCount: Int,
    currentMatchIndex: Int,
    onQueryChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onReplaceOne: (String) -> Unit,
    onReplaceAll: (String) -> Unit,
    onClose: () -> Unit
) {
    var replacement by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Find") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                if (matchCount > 0) "${currentMatchIndex + 1}/$matchCount" else "0/0",
                style = MaterialTheme.typography.bodySmall
            )
            TextButton(onClick = onPrevious, enabled = matchCount > 0) { Text("↑") }
            TextButton(onClick = onNext, enabled = matchCount > 0) { Text("↓") }
            TextButton(onClick = onClose) { Text("✕") }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = replacement,
                onValueChange = { replacement = it },
                label = { Text("Replace with") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(4.dp))
            TextButton(onClick = { onReplaceOne(replacement) }, enabled = matchCount > 0) { Text("Replace") }
            TextButton(onClick = { onReplaceAll(replacement) }, enabled = matchCount > 0) { Text("All") }
        }
    }
}