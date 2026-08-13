package com.example.mobiletexteditor.autosave

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 * Periodically writes the live buffer to a temp file so a crash or a killed
 * process doesn't lose unsaved edits. On next launch, MainActivity checks
 * hasRecoverableSession() and offers to restore before the user starts typing.
 */
class AutosaveManager(
    private val context: Context,
    private val intervalMillis: Long = 10_000L
) {
    private var job: Job? = null
    private val tempFile: File
        get() = File(context.filesDir, "autosave_buffer.tmp")
    private val metaFile: File
        get() = File(context.filesDir, "autosave_meta.tmp")

    /** [getBufferText] is called every tick so we always cache the latest content, not a stale copy. */
    fun start(scope: CoroutineScope, sourceFileId: Long?, getBufferText: () -> String) {
        stop()
        job = scope.launch(Dispatchers.IO) {
            while (true) {
                delay(intervalMillis)
                tempFile.writeText(getBufferText())
                metaFile.writeText((sourceFileId ?: -1L).toString())
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun hasRecoverableSession(): Boolean = tempFile.exists() && tempFile.length() > 0

    fun readRecoveredText(): String = if (tempFile.exists()) tempFile.readText() else ""

    fun readRecoveredFileId(): Long? =
        if (metaFile.exists()) metaFile.readText().toLongOrNull()?.takeIf { it != -1L } else null

    /** Call once recovery has been accepted or explicitly dismissed by the user. */
    fun clearRecoverySession() {
        if (tempFile.exists()) tempFile.delete()
        if (metaFile.exists()) metaFile.delete()
    }
}
