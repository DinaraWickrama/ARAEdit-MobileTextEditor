package com.example.mobiletexteditor.data

import androidx.room.*

@Dao
interface VersionDao {

    @Insert
    suspend fun insert(version: VersionEntity): Long

    @Query("SELECT * FROM versions WHERE fileId = :fileId ORDER BY versionNumber ASC")
    suspend fun allForFile(fileId: Long): List<VersionEntity>

    @Query("SELECT * FROM versions WHERE fileId = :fileId ORDER BY versionNumber DESC LIMIT 1")
    suspend fun latestForFile(fileId: Long): VersionEntity?

    @Query("SELECT MAX(versionNumber) FROM versions WHERE fileId = :fileId")
    suspend fun latestVersionNumber(fileId: Long): Int?
}
