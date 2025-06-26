package com.voicereader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicereader.ui.viewmodels.MainViewModel

@Composable
fun AudioControls(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    val currentPosition by viewModel.currentPosition.collectAsState()
    val currentDocument by viewModel.currentDocument.collectAsState()
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Progress bar
            currentDocument?.let { document ->
                val progress = currentPosition.toFloat() / document.content.length.toFloat()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        style = MaterialTheme.typography.labelSmall
                    )
                    
                    Slider(
                        value = progress,
                        onValueChange = { newProgress ->
                            val newPosition = (newProgress * document.content.length).toInt()
                            viewModel.updatePosition(newPosition)
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                    )
                    
                    Text(
                        text = formatTime(document.content.length),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous chapter
                IconButton(
                    onClick = {
                        // Navigate to previous chapter
                    }
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous Chapter"
                    )
                }
                
                // Rewind
                IconButton(
                    onClick = {
                        val newPosition = (currentPosition - 10).coerceAtLeast(0)
                        viewModel.updatePosition(newPosition)
                    }
                ) {
                    Icon(
                        Icons.Default.Replay10,
                        contentDescription = "Rewind 10 seconds"
                    )
                }
                
                // Play/Pause
                FloatingActionButton(
                    onClick = {
                        isPlaying = !isPlaying
                        if (isPlaying) {
                            // Start TTS
                        } else {
                            // Pause TTS
                        }
                    },
                    containerColor = if (isPlaying) 
                        MaterialTheme.colorScheme.tertiary 
                    else 
                        MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
                
                // Fast forward
                IconButton(
                    onClick = {
                        currentDocument?.let { document ->
                            val newPosition = (currentPosition + 10)
                                .coerceAtMost(document.content.length)
                            viewModel.updatePosition(newPosition)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Forward10,
                        contentDescription = "Forward 10 seconds"
                    )
                }
                
                // Next chapter
                IconButton(
                    onClick = {
                        // Navigate to next chapter
                    }
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next Chapter"
                    )
                }
                
                // Bookmark
                IconButton(
                    onClick = {
                        viewModel.createBookmark()
                    }
                ) {
                    Icon(
                        Icons.Default.BookmarkAdd,
                        contentDescription = "Add Bookmark",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatTime(position: Int): String {
    // Simple time formatting - could be enhanced to show actual reading time
    val minutes = position / 1000
    val seconds = (position % 1000) / 10
    return String.format("%d:%02d", minutes, seconds)
}
