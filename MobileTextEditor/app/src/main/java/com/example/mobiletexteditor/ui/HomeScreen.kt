package com.example.mobiletexteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobiletexteditor.data.FileEntity
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.mobiletexteditor.R
@Composable
fun HomeScreen(
    recentFiles: List<FileEntity>,
    onNewFile: (FileType) -> Unit,
    onOpenFileRequested: () -> Unit,
    onOpenRecentFile: (FileEntity) -> Unit,
    onDeleteRecentFile: (FileEntity) -> Unit
) {
    var showNewFileDialog by remember { mutableStateOf(false) }

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

    Column(modifier = Modifier.fillMaxSize()) {

        // --- Branded header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF1565C0),
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .padding(top = 48.dp, bottom = 28.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.my_logo_foreground),
                        contentDescription = "App logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "ARAEdit",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Write, highlight, and version your code",
                    color = Color(0xFFBBDEFB),
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // --- Action cards ---
            Row(modifier = Modifier.fillMaxWidth()) {
                ActionCard(
                    icon = Icons.Filled.NoteAdd,
                    label = "New File",
                    modifier = Modifier.weight(1f),
                    onClick = { showNewFileDialog = true }
                )
                Spacer(Modifier.width(12.dp))
                ActionCard(
                    icon = Icons.Filled.FolderOpen,
                    label = "Open File",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenFileRequested
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                "RECENT FILES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575)
            )
            Spacer(Modifier.height(8.dp))

            if (recentFiles.isEmpty()) {
                EmptyRecentState()
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(recentFiles) { file ->
                        RecentFileCard(
                            file = file,
                            onClick = { onOpenRecentFile(file) },
                            onDelete = { onDeleteRecentFile(file) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1565C0))
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = Color(0xFF0D47A1))
        }
    }
}

@Composable
private fun EmptyRecentState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Description,
            contentDescription = null,
            tint = Color(0xFFBBDEFB),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "No files yet",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF757575)
        )
        Text(
            "Create or open a file to get started",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
private fun RecentFileCard(
    file: FileEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val (icon, tint) = iconFor(file.displayName)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(tint.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = file.displayName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove ${file.displayName}", tint = Color(0xFF9E9E9E))
            }
        }
    }
}

private fun iconFor(fileName: String): Pair<ImageVector, Color> = when {
    fileName.endsWith(".kt") -> Icons.Filled.Code to Color(0xFF7C4DFF)
    fileName.endsWith(".md") || fileName.endsWith(".markdown") -> Icons.Filled.Description to Color(0xFF1565C0)
    fileName.endsWith(".html") || fileName.endsWith(".htm") -> Icons.Filled.Html to Color(0xFFE64A19)
    fileName.endsWith(".css") -> Icons.Filled.Code to Color(0xFF00897B)
    else -> Icons.Filled.Description to Color(0xFF757575)
}