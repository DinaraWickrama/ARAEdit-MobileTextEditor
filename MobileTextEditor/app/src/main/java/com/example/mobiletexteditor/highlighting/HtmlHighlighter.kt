package com.example.mobiletexteditor.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Pattern-based highlighter for HTML: tag names, attribute names, attribute
 * string values, comments, and doctype declarations. Uses the same layered
 * "apply pattern after pattern" approach as MarkdownHighlighter, since HTML
 * highlighting is naturally about recognizing several independent syntactic
 * shapes (tags, attributes, strings, comments) rather than one token stream.
 *
 * Tag names are looked up structurally (text right after "<" or "</"), not
 * from a fixed keyword set, since custom/unknown HTML tags should still
 * highlight correctly. HtmlKeywords.COMMON_TAGS exists for anyone wanting to
 * add tag-specific coloring or validation later.
 */
object HtmlHighlighter {

    private val COMMENT_COLOR = Color(0xFF6A9955)
    private val DOCTYPE_COLOR = Color(0xFF808080)
    private val TAG_COLOR = Color(0xFF569CD6)
    private val ATTRIBUTE_NAME_COLOR = Color(0xFF9CDCFE)
    private val ATTRIBUTE_VALUE_COLOR = Color(0xFFCE9178)

    private val COMMENT_REGEX = Regex("""<!--[\s\S]*?-->""")
    private val DOCTYPE_REGEX = Regex("""(?i)<!doctype[^>]*>""")

    // Fixed-width lookbehinds only (Android's regex engine, like most Java
    // regex engines, does not support variable-length lookbehind) - kept as
    // two separate patterns rather than one alternation for that reason.
    private val OPEN_TAG_NAME_REGEX = Regex("""(?<=<)[A-Za-z][A-Za-z0-9]*""")
    private val CLOSE_TAG_NAME_REGEX = Regex("""(?<=</)[A-Za-z][A-Za-z0-9]*""")

    private val ATTRIBUTE_NAME_REGEX = Regex("""(?<=\s)[a-zA-Z-]+(?=\s*=)""")
    private val ATTRIBUTE_VALUE_REGEX = Regex(""""[^"]*"|'[^']*'""")

    fun highlight(sourceText: String): AnnotatedString {
        return AnnotatedString.Builder(sourceText).apply {
            // Order matters: broad/structural patterns first, strings and
            // comments last so they win over anything they might overlap.
            applyPattern(this, sourceText, OPEN_TAG_NAME_REGEX, SpanStyle(color = TAG_COLOR, fontWeight = FontWeight.Bold))
            applyPattern(this, sourceText, CLOSE_TAG_NAME_REGEX, SpanStyle(color = TAG_COLOR, fontWeight = FontWeight.Bold))
            applyPattern(this, sourceText, ATTRIBUTE_NAME_REGEX, SpanStyle(color = ATTRIBUTE_NAME_COLOR))
            applyPattern(this, sourceText, ATTRIBUTE_VALUE_REGEX, SpanStyle(color = ATTRIBUTE_VALUE_COLOR))
            applyPattern(this, sourceText, DOCTYPE_REGEX, SpanStyle(color = DOCTYPE_COLOR, fontStyle = FontStyle.Italic))
            applyPattern(this, sourceText, COMMENT_REGEX, SpanStyle(color = COMMENT_COLOR, fontStyle = FontStyle.Italic))
        }.toAnnotatedString()
    }

    private fun applyPattern(
        builder: AnnotatedString.Builder,
        text: String,
        regex: Regex,
        style: SpanStyle
    ) {
        for (match in regex.findAll(text)) {
            if (match.range.isEmpty() && match.value.isEmpty()) continue
            builder.addStyle(style, match.range.first, match.range.last + 1)
        }
    }
}
