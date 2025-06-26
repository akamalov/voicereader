package com.voicereader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val documentPath: String,
    val documentTitle: String,
    val position: Int,
    val chapterTitle: String?,
    val note: String?,
    val timestamp: Date,
    val isAutoBookmark: Boolean = false
)
