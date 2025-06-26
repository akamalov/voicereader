package com.voicereader.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.voicereader.data.database.DocumentEntity
import com.voicereader.ui.components.DocumentCard
import com.voicereader.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onDocumentSelected: () -> Unit,
    onFileSelected: (Uri) -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val recentDocuments by viewModel.recentDocuments.collectAsState()
    
    // Initialize database
    LaunchedEffect(Unit) {
        viewModel.initializeDatabase(context)
    }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { 
            onFileSelected(it)
            onDocumentSelected()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Reader") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    filePickerLauncher.launch("*/*")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (recentDocuments.isEmpty()) {
                EmptyLibraryContent(
                    onAddDocumentClick = {
                        filePickerLauncher.launch("*/*")
                    }
                )
            } else {
                Text(
                    text = "Recent Documents",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentDocuments) { document ->
                        DocumentCard(
                            document = document,
                            onClick = {
                                // Load document and navigate to reader
                                onDocumentSelected()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryContent(
    onAddDocumentClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No documents in your library",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Add EPUB, PDF, or text files to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onAddDocumentClick,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Document")
        }
    }
}
