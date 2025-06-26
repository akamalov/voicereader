package com.voicereader.ui.screens

import android.speech.tts.Voice
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.voicereader.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val speechPitch by viewModel.speechPitch.collectAsState()
    
    var showVoiceSelector by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("All") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Voice Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Voice Selection
                        OutlinedButton(
                            onClick = { showVoiceSelector = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = selectedVoice?.name ?: "Select Voice",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Speech Rate
                        Text(
                            text = "Speech Rate: ${String.format("%.1f", speechRate)}x",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = speechRate,
                            onValueChange = { viewModel.setSpeechRate(it) },
                            valueRange = 0.5f..2.0f,
                            steps = 14
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Speech Pitch
                        Text(
                            text = "Speech Pitch: ${String.format("%.1f", speechPitch)}x",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = speechPitch,
                            onValueChange = { viewModel.setSpeechPitch(it) },
                            valueRange = 0.5f..2.0f,
                            steps = 14
                        )
                    }
                }
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Reading Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Auto-bookmark setting
                        var autoBookmark by remember { mutableStateOf(true) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auto-bookmark on pause")
                            Switch(
                                checked = autoBookmark,
                                onCheckedChange = { autoBookmark = it }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Background playback
                        var backgroundPlayback by remember { mutableStateOf(true) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Background playback")
                            Switch(
                                checked = backgroundPlayback,
                                onCheckedChange = { backgroundPlayback = it }
                            )
                        }
                    }
                }
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Voice Reader v1.0",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "A text-to-speech reader for EPUB, PDF, and text files",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Voice Selector Modal
        if (showVoiceSelector) {
            VoiceSelectorDialog(
                onVoiceSelected = { voice ->
                    viewModel.setVoice(voice)
                    showVoiceSelector = false
                },
                onDismiss = { showVoiceSelector = false }
            )
        }
    }
}

@Composable
fun VoiceSelectorDialog(
    onVoiceSelected: (Voice) -> Unit,
    onDismiss: () -> Unit
) {
    // This would need to be implemented with actual TTS voices
    // For now, showing a placeholder dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Voice") },
        text = {
            Column {
                Text("Voice selection would be implemented here with:")
                Text("• Male voices")
                Text("• Female voices")
                Text("• Language options")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
