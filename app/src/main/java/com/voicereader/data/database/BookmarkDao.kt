package com.voicereader.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE documentPath = :documentPath ORDER BY timestamp DESC")
    fun getBookmarksForDocument(documentPath: String): Flow<List<BookmarkEntity>>
    
    @Query("SELECT * FROM bookmarks WHERE documentPath = :documentPath AND isAutoBookmark = 1 LIMIT 1")
    suspend fun getAutoBookmark(documentPath: String): BookmarkEntity?
    
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)
    
    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)
    
    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
    
    @Query("DELETE FROM bookmarks WHERE documentPath = :documentPath AND isAutoBookmark = 1")
    suspend fun deleteAutoBookmark(documentPath: String)
}
