package com.example.mobiletexteditor.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * versionNumber == 0  -> [delta] holds the FULL base text (the only full copy stored).
 * versionNumber  > 0  -> [delta] holds a unified diff patch relative to the previous version.
 */
@Entity(
    tableName = "versions",
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = ["fileId"],
            childColumns = ["fileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VersionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileId: Long,
    val versionNumber: Int,
    val delta: String,
    val versionName: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
