package com.voicereader.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val path: String,
    val title: String,
    val author: String?,
    val lastReadPosition: Int = 0,
    val totalLength: Int = 0,
    val currentChapter: String?,
    val lastReadTime: Date,
    val readingProgress: Float = 0f,
    val documentType: String // "EPUB", "PDF", "TXT"
)
