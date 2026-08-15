package com.example.mobiletexteditor.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Tokenizer-based highlighter for CSS: comments, strings, property names
 * (keyword-file driven, via CssKeywords), and selectors. Mirrors
 * KotlinHighlighter's structure - a single token regex with numbered groups
 * (not named groups, which are unreliable on Android's regex engine), plus
 * lookahead to decide what a bare word actually is.
 */
object CssHighlighter {

    private val COMMENT_COLOR = Color(0xFF6A9955)
    private val STRING_COLOR = Color(0xFFCE9178)
    private val PROPERTY_COLOR = Color(0xFF9CDCFE)
    private val SELECTOR_COLOR = Color(0xFF569CD6)

    // Group 1 = block comment, 2 = string, 3 = bare word (property or selector)
    private val TOKEN_REGEX = Regex(
        """(/\*[\s\S]*?\*/)""" + "|" +
            """("(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')""" + "|" +
            """([a-zA-Z-]+)"""
    )

    fun highlight(sourceText: String): AnnotatedString {
        return AnnotatedString.Builder(sourceText).apply {
            for (match in TOKEN_REGEX.findAll(sourceText)) {
                val style = when {
                    match.groups[1] != null -> SpanStyle(color = COMMENT_COLOR)
                    match.groups[2] != null -> SpanStyle(color = STRING_COLOR)
                    match.groups[3] != null -> {
                        val word = match.value
                        val next = firstNonSpaceCharAfter(sourceText, match.range.last + 1)
                        when {
                            next == ':' && word in CssKeywords.COMMON_PROPERTIES ->
                                SpanStyle(color = PROPERTY_COLOR, fontWeight = FontWeight.Bold)
                            next == '{' ->
                                SpanStyle(color = SELECTOR_COLOR, fontWeight = FontWeight.Bold)
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

    /** Returns the first non-whitespace character at or after [fromIndex], or null at end of text. */
    private fun firstNonSpaceCharAfter(sourceText: String, fromIndex: Int): Char? {
        var i = fromIndex
        while (i < sourceText.length && sourceText[i].isWhitespace()) i++
        return sourceText.getOrNull(i)
    }
}
