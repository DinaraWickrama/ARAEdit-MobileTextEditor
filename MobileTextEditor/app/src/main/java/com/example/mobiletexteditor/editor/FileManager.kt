package com.example.mobiletexteditor.editor

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import android.provider.OpenableColumns

/**
 * Wraps content:// URI reads/writes via the Storage Access Framework.
 * Handles Open, Save, and Save As. "New" is just an empty in-memory buffer
 * with no URI until the first Save As.
 */
class FileManager(private val context: Context) {

    suspend fun readText(uri: Uri, encoding: String = "UTF-8"): String = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            BufferedReader(InputStreamReader(input, Charset.forName(encoding))).readText()
        } ?: throw IllegalStateException("Could not open input stream for $uri")
    }

    suspend fun writeText(uri: Uri, text: String, encoding: String = "UTF-8") = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
            OutputStreamWriter(output, Charset.forName(encoding)).use { it.write(text) }
        } ?: throw IllegalStateException("Could not open output stream for $uri")
    }

    /** Persist read/write permission across app restarts (needed for "Recent Files"). */
    fun takePersistablePermission(uri: Uri) {
        val flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
            android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)
    }

    fun displayNameOf(uri: Uri): String {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) name = it.getString(nameIndex)
        }
    }
    return name ?: uri.lastPathSegment?.substringAfterLast('/') ?: "untitled"
}
}
