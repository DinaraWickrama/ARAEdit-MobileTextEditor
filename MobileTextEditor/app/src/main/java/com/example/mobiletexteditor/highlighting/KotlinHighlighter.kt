package com.example.mobiletexteditor.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

object KotlinHighlighter {

    private val KEYWORD_COLOR = Color(0xFF569CD6)
    private val STRING_COLOR = Color(0xFFCE9178)
    private val COMMENT_COLOR = Color(0xFF6A9955)
    private val ANNOTATION_COLOR = Color(0xFF9CDCFE)
    private val FUNCTION_COLOR = Color(0xFFDCDCAA)

    // Numbered groups (not named) — named groups are unreliable on Android's regex engine.
    // Group 1 = block comment, 2 = line comment, 3 = string, 4 = annotation, 5 = word
    private val TOKEN_REGEX = Regex(
        """(/\*[\s\S]*?\*/)""" + "|" +
                """(//[^\n]*)""" + "|" +
                """("(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')""" + "|" +
                """(@[A-Za-z_][A-Za-z0-9_]*)""" + "|" +
                """(\b[A-Za-z_][A-Za-z0-9_]*\b)"""
    )

    fun highlight(sourceText: String): AnnotatedString {
        return AnnotatedString.Builder(sourceText).apply {
            for (match in TOKEN_REGEX.findAll(sourceText)) {
                val style = when {
                    match.groups[1] != null -> SpanStyle(color = COMMENT_COLOR)
                    match.groups[2] != null -> SpanStyle(color = COMMENT_COLOR)
                    match.groups[3] != null -> SpanStyle(color = STRING_COLOR)
                    match.groups[4] != null -> SpanStyle(color = ANNOTATION_COLOR)
                    match.groups[5] != null -> {
                        val word = match.value
                        when {
                            word in KotlinKeywords.ALL ->
                                SpanStyle(color = KEYWORD_COLOR, fontWeight = FontWeight.Bold)
                            isFollowedByParenthesis(sourceText, match.range.last + 1) ->
                                SpanStyle(color = FUNCTION_COLOR)
                            else -> null
                        }
                    }
                    else -> null
                }
                if (style != null) {
                    addStyle(style, match.range.first, match.range.last + 1)
                }
            }
        }.toAnnotatedString()
    }

    /** True if the next non-space character after [fromIndex] is '(' — i.e. this identifier
     *  is being called or declared as a function, like main( or println(. */
    private fun isFollowedByParenthesis(sourceText: String, fromIndex: Int): Boolean {
        var i = fromIndex
        while (i < sourceText.length && sourceText[i] == ' ') i++
        return i < sourceText.length && sourceText[i] == '('
    }
}