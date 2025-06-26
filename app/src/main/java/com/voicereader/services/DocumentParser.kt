package com.voicereader.services

import android.content.Context
import android.net.Uri
import com.voicereader.data.models.*
import nl.siegmann.epublib.epub.EpubReader
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.InputStream

class DocumentParser(private val context: Context) {
    
    init {
        PDFBoxResourceLoader.init(context)
    }
    
    suspend fun parseDocument(uri: Uri): DocumentContent? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            
            when {
                mimeType?.contains("epub") == true -> parseEpub(uri)
                mimeType?.contains("pdf") == true -> parsePdf(uri)
                mimeType?.contains("text") == true -> parseText(uri)
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private suspend fun parseEpub(uri: Uri): DocumentContent? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val book = EpubReader().readEpub(inputStream)
            
            val title = book.title ?: "Unknown Title"
            val author = book.metadata.authors.firstOrNull()?.let { "${it.firstname} ${it.lastname}" }
            
            val contentBuilder = StringBuilder()
            val chapters = mutableListOf<Chapter>()
            val tocItems = mutableListOf<TOCItem>()
            var currentPosition = 0
            
            // Process spine (reading order)
            book.spine.spineReferences.forEachIndexed { index, spineRef ->
                val resource = spineRef.resource
                val chapterContent = String(resource.data, Charsets.UTF_8)
                    .replace(Regex("<[^>]*>"), "") // Remove HTML tags
                    .replace(Regex("\\s+"), " ")
                    .trim()
                
                if (chapterContent.isNotBlank()) {
                    val chapterTitle = resource.title ?: "Chapter ${index + 1}"
                    val startPos = currentPosition
                    val endPos = currentPosition + chapterContent.length
                    
                    chapters.add(Chapter(chapterTitle, chapterContent, startPos, endPos))
                    tocItems.add(TOCItem(chapterTitle, 0, startPos))
                    
                    contentBuilder.append(chapterContent).append("\n\n")
                    currentPosition = endPos + 2
                }
            }
            
            DocumentContent(
                title = title,
                author = author,
                content = contentBuilder.toString(),
                tableOfContents = tocItems,
                chapters = chapters,
                documentType = DocumentType.EPUB
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private suspend fun parsePdf(uri: Uri): DocumentContent? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val document = PDDocument.load(inputStream)
            
            val stripper = PDFTextStripper()
            val content = stripper.getText(document)
            
            // Simple chapter detection based on page breaks or headings
            val chapters = mutableListOf<Chapter>()
            val tocItems = mutableListOf<TOCItem>()
            
            // For PDF, we'll create chapters based on pages
            val pageCount = document.numberOfPages
            for (i in 1..pageCount) {
                stripper.startPage = i
                stripper.endPage = i
                val pageContent = stripper.getText(document)
                
                if (pageContent.trim().isNotEmpty()) {
                    val chapterTitle = "Page $i"
                    val startPos = (i - 1) * (content.length / pageCount)
                    val endPos = i * (content.length / pageCount)
                    
                    chapters.add(Chapter(chapterTitle, pageContent, startPos, endPos))
                    tocItems.add(TOCItem(chapterTitle, 0, startPos))
                }
            }
            
            document.close()
            
            DocumentContent(
                title = "PDF Document",
                author = null,
                content = content,
                tableOfContents = tocItems,
                chapters = chapters,
                documentType = DocumentType.PDF
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private suspend fun parseText(uri: Uri): DocumentContent? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
            
            // Simple chapter detection based on blank lines or common patterns
            val chapters = mutableListOf<Chapter>()
            val tocItems = mutableListOf<TOCItem>()
            
            val lines = content.split("\n")
            var currentChapter = StringBuilder()
            var chapterCount = 1
            var currentPosition = 0
            
            for (line in lines) {
                if (line.trim().isEmpty() && currentChapter.isNotEmpty()) {
                    // End of chapter
                    val chapterContent = currentChapter.toString().trim()
                    val chapterTitle = "Section $chapterCount"
                    val startPos = currentPosition - chapterContent.length
                    
                    chapters.add(Chapter(chapterTitle, chapterContent, startPos, currentPosition))
                    tocItems.add(TOCItem(chapterTitle, 0, startPos))
                    
                    currentChapter.clear()
                    chapterCount++
                } else {
                    currentChapter.append(line).append("\n")
                }
                currentPosition += line.length + 1
            }
            
            // Add final chapter if exists
            if (currentChapter.isNotEmpty()) {
                val chapterContent = currentChapter.toString().trim()
                val chapterTitle = "Section $chapterCount"
                val startPos = currentPosition - chapterContent.length
                
                chapters.add(Chapter(chapterTitle, chapterContent, startPos, currentPosition))
                tocItems.add(TOCItem(chapterTitle, 0, startPos))
            }
            
            DocumentContent(
                title = "Text Document",
                author = null,
                content = content,
                tableOfContents = tocItems,
                chapters = chapters,
                documentType = DocumentType.TEXT
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
