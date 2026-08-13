package com.example.mobiletexteditor.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Insert
    suspend fun insert(file: FileEntity): Long

    @Update
    suspend fun update(file: FileEntity)

    @Query("SELECT * FROM files ORDER BY lastOpenedAt DESC")
    fun recentFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE fileId = :fileId LIMIT 1")
    suspend fun getById(fileId: Long): FileEntity?

    @Query("SELECT * FROM files WHERE uriString = :uriString LIMIT 1")
    suspend fun findByUri(uriString: String): FileEntity?

    @Query("UPDATE files SET isReadOnly = :readOnly WHERE fileId = :fileId")
    suspend fun setReadOnly(fileId: Long, readOnly: Boolean)

    @Query("UPDATE files SET lastOpenedAt = :timestamp WHERE fileId = :fileId")
    suspend fun touch(fileId: Long, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(file: FileEntity)
}
