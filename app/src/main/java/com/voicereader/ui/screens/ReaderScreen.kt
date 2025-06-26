package com.voicereader.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.voicereader.ui.components.AudioControls
import com.voicereader.ui.components.BookmarkPanel
import com.voicereader.ui.components.TOCPanel
import com.voicereader.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val currentDocument by viewModel.currentDocument.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    
    var showTOC by remember { mutableStateOf(false) }
    var showBookmarks by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = currentDocument?.title ?: "Voice Reader",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showTOC = true }) {
                        Icon(Icons.Default.List, contentDescription = "Table of Contents")
                    }
                    IconButton(onClick = { showBookmarks = true }) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            AudioControls(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            currentDocument?.let { document ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Document info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = document.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            document.author?.let { author ->
                                Text(
                                    text = "by $author",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val progress = currentPosition.toFloat() / document.content.length.toFloat()
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Text(
                                text = "${(progress * 100).toInt()}% complete",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Text content
                    val displayText = remember(currentPosition, document.content) {
                        val startPos = currentPosition.coerceAtMost(document.content.length)
                        val endPos = (startPos + 1000).coerceAtMost(document.content.length)
                        document.content.substring(startPos, endPos)
                    }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
                
                // Table of Contents Modal
                if (showTOC) {
                    TOCPanel(
                        tableOfContents = document.tableOfContents,
                        onItemClick = { tocItem ->
                            viewModel.jumpToTOCItem(tocItem)
                            showTOC = false
                        },
                        onDismiss = { showTOC = false }
                    )
                }
                
                // Bookmarks Modal
                if (showBookmarks) {
                    BookmarkPanel(
                        viewModel = viewModel,
                        onDismiss = { showBookmarks = false }
                    )
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No document loaded",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
