package com.voicereader.data.models

data class DocumentContent(
    val title: String,
    val author: String?,
    val content: String,
    val tableOfContents: List<TOCItem>,
    val chapters: List<Chapter>,
    val documentType: DocumentType
)

data class TOCItem(
    val title: String,
    val level: Int,
    val startPosition: Int,
    val children: List<TOCItem> = emptyList()
)

data class Chapter(
    val title: String,
    val content: String,
    val startPosition: Int,
    val endPosition: Int
)

enum class DocumentType {
    EPUB, PDF, TEXT
}
