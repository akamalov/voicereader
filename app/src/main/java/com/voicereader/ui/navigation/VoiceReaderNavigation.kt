package com.voicereader.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.voicereader.ui.screens.LibraryScreen
import com.voicereader.ui.screens.ReaderScreen
import com.voicereader.ui.screens.SettingsScreen
import com.voicereader.ui.viewmodels.MainViewModel

@Composable
fun VoiceReaderNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel,
    onFileSelected: (Uri) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "library"
    ) {
        composable("library") {
            LibraryScreen(
                viewModel = viewModel,
                onDocumentSelected = { 
                    navController.navigate("reader")
                },
                onFileSelected = onFileSelected,
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("reader") {
            ReaderScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
