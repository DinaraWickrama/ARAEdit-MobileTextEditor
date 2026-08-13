package com.example.mobiletexteditor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Metadata for a file managed by the editor.
 * The actual "version 0" content lives in [VersionEntity] with versionNumber = 0
 * so that everything after the base snapshot can be stored as a diff.
 */
@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true)
    val fileId: Long = 0,
    val displayName: String,
    val uriString: String,       // persisted content:// or file:// path
    val isReadOnly: Boolean = false,
    val lastOpenedAt: Long = System.currentTimeMillis(),
    val encoding: String = "UTF-8"
)
