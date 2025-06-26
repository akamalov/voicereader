package com.voicereader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.voicereader.data.database.BookmarkEntity
import com.voicereader.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookmarkPanel(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    var showAddBookmarkDialog by remember { mutableStateOf(false) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bookmarks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row {
                        IconButton(onClick = { showAddBookmarkDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Bookmark")
                        }
                        
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
                
                Divider()
                
                // Bookmarks List
                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "No bookmarks yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "Add bookmarks to save your reading positions",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookmarks) { bookmark ->
                            BookmarkItemRow(
                                bookmark = bookmark,
                                onClick = { 
                                    viewModel.jumpToBookmark(bookmark)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add Bookmark Dialog
    if (showAddBookmarkDialog) {
        AddBookmarkDialog(
            onConfirm = { note ->
                viewModel.createBookmark(note)
                showAddBookmarkDialog = false
            },
            onDismiss = { showAddBookmarkDialog = false }
        )
    }
}

@Composable
private fun BookmarkItemRow(
    bookmark: BookmarkEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (bookmark.isAutoBookmark) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (bookmark.isAutoBookmark) Icons.Default.History else Icons.Default.Bookmark,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (bookmark.isAutoBookmark) "Auto-bookmark" else 
                           bookmark.note ?: "Bookmark",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                bookmark.chapterTitle?.let { chapter ->
                    Text(
                        text = chapter,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        .format(bookmark.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddBookmarkDialog(
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bookmark") },
        text = {
            Column {
                Text("Add a note for this bookmark (optional):")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Bookmark note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    onConfirm(note.takeIf { it.isNotBlank() })
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
