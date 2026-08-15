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
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private enum class Screen { HOME, EDITOR, HISTORY }
private val ARAEditColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF1976D2),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1A1A1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE3F2FD),
    onSurfaceVariant = Color(0xFF1A1A1A)
)
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

            MaterialTheme (colorScheme = ARAEditColors){
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