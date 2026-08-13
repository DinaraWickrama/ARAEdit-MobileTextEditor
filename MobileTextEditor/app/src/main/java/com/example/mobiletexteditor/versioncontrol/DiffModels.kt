package com.example.mobiletexteditor.versioncontrol

enum class DiffLineType { EQUAL, ADDED, DELETED, CHANGED }

data class DiffLine(
    val type: DiffLineType,
    val oldText: String,
    val newText: String
)