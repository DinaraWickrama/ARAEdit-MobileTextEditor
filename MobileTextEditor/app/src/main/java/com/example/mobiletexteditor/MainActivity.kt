package com.example.mobiletexteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiletexteditor.ui.EditorScreen
import com.example.mobiletexteditor.ui.EditorViewModel
import com.example.mobiletexteditor.ui.HomeScreen
import com.example.mobiletexteditor.versioncontrol.DiffViewScreen

private enum class Screen { HOME, EDITOR, HISTORY }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: EditorViewModel = viewModel()
            var currentScreen by remember { mutableStateOf(Screen.HOME) }

            val uiState by viewModel.uiState.collectAsState()
            val recentFiles by viewModel.recentFiles.collectAsState(initial = emptyList())

            val openDocumentLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    viewModel.openFile(uri)
                    currentScreen = Screen.EDITOR
                }
            }

            val createDocumentLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("*/*")
            ) { uri ->
                uri?.let { viewModel.saveAs(it) }
            }

            MaterialTheme {
                Surface(modifier = Modifier) {
                    when (currentScreen) {
                        Screen.HOME -> HomeScreen(
                            recentFiles = recentFiles,
                            onNewFile = { fileType ->
                                viewModel.newFile(fileType)
                                currentScreen = Screen.EDITOR
                            },
                            onOpenFileRequested = { openDocumentLauncher.launch(arrayOf("*/*")) },
                            onOpenRecentFile = { file ->
                                viewModel.openRecentFile(file)
                                currentScreen = Screen.EDITOR
                            },
                            onDeleteRecentFile = { file -> viewModel.deleteRecentFile(file) }
                        )

                        Screen.EDITOR -> EditorScreen(
                            viewModel = viewModel,
                            onSaveAsRequested = { suggestedFileName ->
                                createDocumentLauncher.launch(suggestedFileName)
                            },
                            onShowVersionHistory = { currentScreen = Screen.HISTORY },
                            onBackToHome = { currentScreen = Screen.HOME }
                        )

                        Screen.HISTORY -> DiffViewScreen(
                            viewModel = viewModel,
                            versionHistory = uiState.versionHistory,
                            onBack = { currentScreen = Screen.EDITOR }
                        )
                    }
                }
            }
        }
    }
}