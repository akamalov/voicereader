package com.voicereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.voicereader.ui.navigation.VoiceReaderNavigation
import com.voicereader.ui.theme.VoiceReaderTheme
import com.voicereader.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            VoiceReaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainViewModel = viewModel()
                    VoiceReaderNavigation(
                        viewModel = viewModel,
                        onFileSelected = { uri ->
                            viewModel.loadDocument(uri, this@MainActivity)
                        }
                    )
                }
            }
        }
    }
}
