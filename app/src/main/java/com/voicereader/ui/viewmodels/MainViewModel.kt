package com.voicereader.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.speech.tts.Voice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicereader.data.database.BookmarkEntity
import com.voicereader.data.database.DocumentEntity
import com.voicereader.data.database.ReaderDatabase
import com.voicereader.data.models.DocumentContent
import com.voicereader.data.models.TOCItem
import com.voicereader.services.DocumentParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel : ViewModel() {
    private val _currentDocument = MutableStateFlow<DocumentContent?>(null)
    val currentDocument: StateFlow<DocumentContent?> = _currentDocument
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition
    
    private val _bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkEntity>> = _bookmarks
    
    private val _recentDocuments = MutableStateFlow<List<DocumentEntity>>(emptyList())
    val recentDocuments: StateFlow<List<DocumentEntity>> = _recentDocuments
    
    private val _selectedVoice = MutableStateFlow<Voice?>(null)
    val selectedVoice: StateFlow<Voice?> = _selectedVoice
    
    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate
    
    private val _speechPitch = MutableStateFlow(1.0f)
    val speechPitch: StateFlow<Float> = _speechPitch
    
    private var database: ReaderDatabase? = null
    private var documentParser: DocumentParser? = null
    private var currentDocumentPath: String? = null
    
    fun initializeDatabase(context: Context) {
        database = ReaderDatabase.getDatabase(context)
        documentParser = DocumentParser(context)
        loadRecentDocuments()
    }
    
    fun loadDocument(uri: Uri, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val parser = documentParser ?: DocumentParser(context)
                val documentContent = parser.parseDocument(uri)
                
                if (documentContent != null) {
                    _currentDocument.value = documentContent
                    currentDocumentPath = uri.toString()
                    
                    // Save to database
                    val documentEntity = DocumentEntity(
                        path = uri.toString(),
                        title = documentContent.title,
                        author = documentContent.author,
                        lastReadTime = Date(),
                        documentType = documentContent.documentType.name,
                        totalLength = documentContent.content.length,
                        currentChapter = null
                    )
                    
                    database?.documentDao()?.insertDocument(documentEntity)
                    
                    // Load existing bookmark
                    loadBookmarks(uri.toString())
                    loadLastReadingPosition(uri.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadRecentDocuments() {
        viewModelScope.launch {
            database?.documentDao()?.getAllDocuments()?.collect { documents ->
                _recentDocuments.value = documents
            }
        }
    }
    
    private fun loadBookmarks(documentPath: String) {
        viewModelScope.launch {
            database?.bookmarkDao()?.getBookmarksForDocument(documentPath)?.collect { bookmarks ->
                _bookmarks.value = bookmarks
            }
        }
    }
    
    private suspend fun loadLastReadingPosition(documentPath: String) {
        val document = database?.documentDao()?.getDocument(documentPath)
        document?.let {
            _currentPosition.value = it.lastReadPosition
        }
    }
    
    fun createBookmark(note: String? = null) {
        viewModelScope.launch {
            val document = _currentDocument.value
            val path = currentDocumentPath
            
            if (document != null && path != null) {
                val currentChapter = getCurrentChapter()
                val bookmark = BookmarkEntity(
                    documentPath = path,
                    documentTitle = document.title,
                    position = _currentPosition.value,
                    chapterTitle = currentChapter?.title,
                    note = note,
                    timestamp = Date(),
                    isAutoBookmark = false
                )
                
                database?.bookmarkDao()?.insertBookmark(bookmark)
            }
        }
    }
    
    fun createAutoBookmark() {
        viewModelScope.launch {
            val document = _currentDocument.value
            val path = currentDocumentPath
            
            if (document != null && path != null) {
                // Delete existing auto bookmark
                database?.bookmarkDao()?.deleteAutoBookmark(path)
                
                val currentChapter = getCurrentChapter()
                val bookmark = BookmarkEntity(
                    documentPath = path,
                    documentTitle = document.title,
                    position = _currentPosition.value,
                    chapterTitle = currentChapter?.title,
                    note = null,
                    timestamp = Date(),
                    isAutoBookmark = true
                )
                
                database?.bookmarkDao()?.insertBookmark(bookmark)
            }
        }
    }
    
    fun jumpToBookmark(bookmark: BookmarkEntity) {
        _currentPosition.value = bookmark.position
        updateReadingProgress()
    }
    
    fun jumpToTOCItem(tocItem: TOCItem) {
        _currentPosition.value = tocItem.startPosition
        updateReadingProgress()
    }
    
    fun updatePosition(position: Int) {
        _currentPosition.value = position
        updateReadingProgress()
    }
    
    private fun updateReadingProgress() {
        viewModelScope.launch {
            val document = _currentDocument.value
            val path = currentDocumentPath
            
            if (document != null && path != null) {
                val progress = _currentPosition.value.toFloat() / document.content.length.toFloat()
                database?.documentDao()?.updateReadingProgress(
                    path = path,
                    position = _currentPosition.value,
                    progress = progress
                )
            }
        }
    }
    
    private fun getCurrentChapter(): com.voicereader.data.models.Chapter? {
        val document = _currentDocument.value ?: return null
        val position = _currentPosition.value
        
        return document.chapters.find { chapter ->
            position >= chapter.startPosition && position <= chapter.endPosition
        }
    }
    
    fun setVoice(voice: Voice) {
        _selectedVoice.value = voice
    }
    
    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate
    }
    
    fun setSpeechPitch(pitch: Float) {
        _speechPitch.value = pitch
    }
    
    fun getCurrentText(): String {
        val document = _currentDocument.value ?: return ""
        val position = _currentPosition.value
        val remainingText = document.content.substring(
            position.coerceAtMost(document.content.length)
        )
        
        // Return next few sentences for reading
        val sentences = remainingText.split(Regex("[.!?]+")).take(10)
        return sentences.joinToString(". ") + if (sentences.size == 10) "..." else ""
    }
}
