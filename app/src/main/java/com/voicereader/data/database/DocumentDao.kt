package com.voicereader.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY lastReadTime DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents WHERE path = :path")
    suspend fun getDocument(path: String): DocumentEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)
    
    @Update
    suspend fun updateDocument(document: DocumentEntity)
    
    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
    
    @Query("UPDATE documents SET lastReadPosition = :position, readingProgress = :progress WHERE path = :path")
    suspend fun updateReadingProgress(path: String, position: Int, progress: Float)
}
