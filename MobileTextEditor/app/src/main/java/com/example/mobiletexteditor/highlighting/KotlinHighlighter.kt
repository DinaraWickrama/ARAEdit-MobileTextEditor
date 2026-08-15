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
    private val NUMBER_COLOR = Color(0xFFB5CEA8)

    // Numbered groups (not named) — named groups are unreliable on Android's regex engine.
    // Group 1 = raw/triple-quoted string, 2 = block comment, 3 = line comment,
    // 4 = regular string, 5 = annotation, 6 = number, 7 = word
    //
    // The raw-string alternative MUST come before the regular-string alternative:
    // regex alternation tries branches left-to-right at each position, so without
    // this ordering a leading """ would be mis-tokenized by the regular-string
    // branch as an empty "" followed by a stray, unmatched " character.
    private val TOKEN_REGEX = Regex(
        "(\"\"\"[\\s\\S]*?\"\"\")" + "|" +
            """(/\*[\s\S]*?\*/)""" + "|" +
            """(//[^\n]*)""" + "|" +
            """("(?:[^"\\]|\\.)*"|'(?:[^'\\]|\\.)*')""" + "|" +
            """(@[A-Za-z_][A-Za-z0-9_]*)""" + "|" +
            """(\b\d+(?:\.\d+)?[fFLuU]?\b)""" + "|" +
            """(\b[A-Za-z_][A-Za-z0-9_]*\b)"""
    )

    fun highlight(sourceText: String): AnnotatedString {
        return AnnotatedString.Builder(sourceText).apply {
            for (match in TOKEN_REGEX.findAll(sourceText)) {
                val style = when {
                    match.groups[1] != null -> SpanStyle(color = STRING_COLOR)     // raw string
                    match.groups[2] != null -> SpanStyle(color = COMMENT_COLOR)    // block comment
                    match.groups[3] != null -> SpanStyle(color = COMMENT_COLOR)    // line comment
                    match.groups[4] != null -> SpanStyle(color = STRING_COLOR)     // regular string
                    match.groups[5] != null -> SpanStyle(color = ANNOTATION_COLOR) // annotation
                    match.groups[6] != null -> SpanStyle(color = NUMBER_COLOR)     // number
                    match.groups[7] != null -> {
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
