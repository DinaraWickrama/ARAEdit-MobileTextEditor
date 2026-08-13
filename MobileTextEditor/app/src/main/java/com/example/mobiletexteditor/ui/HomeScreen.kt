package com.example.mobiletexteditor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobiletexteditor.data.FileEntity

/**
 * The app's landing page. Two entry points into editing, per the spec:
 *  - "New File" -> a blank editor
 *  - "Open File..." -> the system file browser (for files not yet tracked by the app)
 * Recent files (already tracked, with saved version history) are listed below and
 * open directly on tap, without going through the system picker again.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    recentFiles: List<FileEntity>,
    onNewFile: (FileType) -> Unit,
    onOpenFileRequested: () -> Unit,
    onOpenRecentFile: (FileEntity) -> Unit,
    onDeleteRecentFile: (FileEntity) -> Unit
) {
    var showNewFileDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ARAEdit") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showNewFileDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("+ New File", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenFileRequested,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Open File...", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Recent Files",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            if (recentFiles.isEmpty()) {
                Text(
                    "No files yet — start with New File or Open File above.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(recentFiles) { file ->
                        RecentFileRow(
                            file = file,
                            onClick = { onOpenRecentFile(file) },
                            onDelete = { onDeleteRecentFile(file) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
    if (showNewFileDialog) {
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text("New file type") },
            text = {
                Column {
                    val options = listOf(
                        FileType.KOTLIN to "Kotlin (.kt)",
                        FileType.MARKDOWN to "Markdown (.md)",
                        FileType.HTML to "HTML (.html)",
                        FileType.CSS to "CSS (.css)",
                        FileType.PLAIN to "Plain text (.txt)"
                    )
                    options.forEach { (type, label) ->
                        TextButton(
                            onClick = { showNewFileDialog = false; onNewFile(type) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(label, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) { Text("Cancel") }
            }
        )
    }
}


@Composable
private fun RecentFileRow(
    file: FileEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Icon(Icons.Filled.Description, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(
            text = file.displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Remove ${file.displayName}")
        }
    }
}