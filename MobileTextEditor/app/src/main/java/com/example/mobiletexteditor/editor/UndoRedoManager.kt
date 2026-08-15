package com.example.mobiletexteditor.editor

/**
 * Session-only undo/redo stack. This is intentionally separate from the
 * persistent, disk-backed DeltaManager version control system:
 *  - UndoRedoManager: granular, in-memory, cleared when the file closes.
 *  - DeltaManager: coarse, persisted snapshots/patches the user explicitly saves.
 */
class UndoRedoManager(private val maxHistory: Int = 200) {

    private val undoStack = ArrayDeque<String>()
    private val redoStack = ArrayDeque<String>()
    private var current: String = ""

    fun initialize(initialText: String) {
        undoStack.clear()
        redoStack.clear()
        current = initialText
    }

    /** Call after each meaningful edit (e.g. debounced, not per-keystroke). */
    fun recordEdit(newText: String) {
        if (newText == current) return
        undoStack.addLast(current)
        if (undoStack.size > maxHistory) undoStack.removeFirst()
        redoStack.clear()
        current = newText
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun undo(): String {
        if (!canUndo()) return current
        redoStack.addLast(current)
        current = undoStack.removeLast()
        return current
    }

    fun redo(): String {
        if (!canRedo()) return current
        undoStack.addLast(current)
        current = redoStack.removeLast()
        return current
    }
    /** Updates the live buffer WITHOUT creating a new undo checkpoint — used while
     *  the user is still in the middle of typing a single word. */
    fun updateLiveText(newText: String) {
        current = newText
    }

    fun currentText(): String = current
}
