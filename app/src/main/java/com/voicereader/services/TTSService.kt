package com.voicereader.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class TTSService : Service(), TextToSpeech.OnInitListener {
    
    private var textToSpeech: TextToSpeech? = null
    private val binder = TTSBinder()
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition
    
    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices
    
    private var currentText: String = ""
    private var currentSentences: List<String> = emptyList()
    private var currentSentenceIndex = 0
    
    inner class TTSBinder : Binder() {
        fun getService(): TTSService = this@TTSService
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                // Set default language
                val result = tts.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || 
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to English
                    tts.setLanguage(Locale.ENGLISH)
                }
                
                // Get available voices
                val voices = tts.voices?.toList() ?: emptyList()
                _availableVoices.value = voices
                
                _isInitialized.value = true
            }
        }
    }
    
    fun setText(text: String) {
        currentText = text
        currentSentences = text.split(Regex("[.!?]+")).filter { it.trim().isNotEmpty() }
        currentSentenceIndex = 0
        _currentPosition.value = 0
    }
    
    fun speak() {
        if (_isInitialized.value && currentSentences.isNotEmpty()) {
            _isPlaying.value = true
            speakNextSentence()
        }
    }
    
    private fun speakNextSentence() {
        if (currentSentenceIndex < currentSentences.size) {
            val sentence = currentSentences[currentSentenceIndex].trim()
            textToSpeech?.speak(
                sentence,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "sentence_$currentSentenceIndex"
            )
            
            // Update position
            val position = currentSentences.take(currentSentenceIndex + 1)
                .sumOf { it.length + 1 } // +1 for punctuation
            _currentPosition.value = position
            
            currentSentenceIndex++
            
            // Schedule next sentence
            textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                
                override fun onDone(utteranceId: String?) {
                    if (currentSentenceIndex < currentSentences.size) {
                        speakNextSentence()
                    } else {
                        _isPlaying.value = false
                    }
                }
                
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                }
            })
        }
    }
    
    fun pause() {
        textToSpeech?.stop()
        _isPlaying.value = false
    }
    
    fun resume() {
        if (!_isPlaying.value) {
            speak()
        }
    }
    
    fun stop() {
        textToSpeech?.stop()
        _isPlaying.value = false
        currentSentenceIndex = 0
        _currentPosition.value = 0
    }
    
    fun setVoice(voice: Voice) {
        textToSpeech?.voice = voice
    }
    
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
    }
    
    fun setPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch)
    }
    
    fun getMaleVoices(): List<Voice> {
        return _availableVoices.value.filter { voice ->
            voice.name.contains("male", ignoreCase = true) && 
            !voice.name.contains("female", ignoreCase = true)
        }
    }
    
    fun getFemaleVoices(): List<Voice> {
        return _availableVoices.value.filter { voice ->
            voice.name.contains("female", ignoreCase = true)
        }
    }
    
    fun seekToPosition(position: Int) {
        // Calculate which sentence contains this position
        var currentPos = 0
        for (i in currentSentences.indices) {
            val sentenceLength = currentSentences[i].length + 1
            if (currentPos + sentenceLength > position) {
                currentSentenceIndex = i
                _currentPosition.value = position
                return
            }
            currentPos += sentenceLength
        }
    }
    
    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
