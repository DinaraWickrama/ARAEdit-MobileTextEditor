package com.example.mobiletexteditor.versioncontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobiletexteditor.data.VersionEntity
import com.example.mobiletexteditor.ui.EditorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffViewScreen(
    viewModel: EditorViewModel,
    versionHistory: List<VersionEntity>,
    onBack: () -> Unit
) {
    var selectedFrom by remember { mutableStateOf<Int?>(null) }
    var selectedTo by remember { mutableStateOf<Int?>(null) }
    var diffRows by remember { mutableStateOf<List<DiffLine>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Version History") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            Text("Tap two versions to compare, or Restore to roll back.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(versionHistory) { version ->
                    VersionRow(
                        version = version,
                        isSelected = version.versionNumber == selectedFrom || version.versionNumber == selectedTo,
                        onSelect = {
                            selectedFrom = selectedTo
                            selectedTo = version.versionNumber
                        },
                        onRestore = { viewModel.rollbackTo(version.versionNumber) }
                    )
                }
            }

            val from = selectedFrom
            val to = selectedTo
            if (from != null && to != null) {
                Button(onClick = {
                    coroutineScope.launch {
                        diffRows = viewModel.structuredDiffBetween(from, to)
                    }
                }) { Text("Compare v$from -> v$to") }
            }

            if (diffRows.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Divider()
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Version $from", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text("Version $to", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                }
                Divider()
                LazyColumn(modifier = Modifier.height(260.dp)) {
                    items(diffRows) { row ->
                        DiffRowView(row)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Legend()
            }
        }
    }
}

@Composable
private fun DiffRowView(row: DiffLine) {
    val oldBg = when (row.type) {
        DiffLineType.DELETED, DiffLineType.CHANGED -> Color(0xFFFFCDD2)
        else -> Color.Transparent
    }
    val newBg = when (row.type) {
        DiffLineType.ADDED, DiffLineType.CHANGED -> Color(0xFFC8E6C9)
        else -> Color.Transparent
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = row.oldText,
            modifier = Modifier.weight(1f).background(oldBg).padding(4.dp),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = row.newText,
            modifier = Modifier.weight(1f).background(newBg).padding(4.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Legend() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        LegendItem(Color(0xFFC8E6C9), "Added")
        LegendItem(Color(0xFFFFCDD2), "Deleted")
        LegendItem(Color(0xFFFFF59D), "Changed (both sides shaded)")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row {
        Box(modifier = Modifier.size(12.dp).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun VersionRow(
    version: VersionEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onRestore: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.clickable(onClick = onSelect)) {
            Text(
                "v${version.versionNumber}" + (version.versionName?.let { " — $it" } ?: ""),
                color = if (isSelected) Color(0xFF1565C0) else Color.Unspecified
            )
        }
        TextButton(onClick = onRestore) { Text("Restore") }
    }
}