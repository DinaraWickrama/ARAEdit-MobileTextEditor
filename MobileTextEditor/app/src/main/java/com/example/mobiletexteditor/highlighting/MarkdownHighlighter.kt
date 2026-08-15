package com.example.mobiletexteditor.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration

/**
 * Line/pattern based highlighter for common Markdown constructs.
 * This drives in-editor highlighting; MarkdownPreviewRenderer (separate,
 * optional) handles turning the same text into a rendered preview.
 */
object MarkdownHighlighter {
    private val HEADER_COLOR = Color(0xFF4EC9B0)
    private val BOLD_COLOR = Color(0xFFD7BA7D)
    private val CODE_COLOR = Color(0xFFCE9178)
    private val LINK_COLOR = Color(0xFF569CD6)
    private val QUOTE_COLOR = Color(0xFF808080)
    private val LIST_COLOR = Color(0xFF4EC9B0)
    private val STRIKETHROUGH_COLOR = Color(0xFF808080)
    private val HR_COLOR = Color(0xFF808080)

    private val HEADER_REGEX = Regex("""(?m)^#{1,6}\s.*$""")
    private val BOLD_REGEX = Regex("""\*\*[^*]+\*\*|__[^_]+__""")
    private val ITALIC_REGEX = Regex("""(?<!\*)\*[^*\n]+\*(?!\*)|(?<!_)_[^_\n]+_(?!_)""")
    private val INLINE_CODE_REGEX = Regex("""`[^`\n]+`""")
    private val CODE_FENCE_REGEX = Regex("""(?s)```.*?```""")
    private val LINK_REGEX = Regex("""\[[^]]+]\([^)]+\)""")
    private val BLOCKQUOTE_REGEX = Regex("""(?m)^>.*$""")
    private val LIST_ITEM_REGEX = Regex("""(?m)^\s*[-*+]\s+.*$""")

    // New: strikethrough (~~text~~), horizontal rules (---, ***, ___ on their
    // own line), and ordered/numbered list items (1. item, 2. item, ...).
    private val STRIKETHROUGH_REGEX = Regex("""~~[^~\n]+~~""")
    private val HORIZONTAL_RULE_REGEX = Regex("""(?m)^ {0,3}(-{3,}|\*{3,}|_{3,})\s*$""")
    private val ORDERED_LIST_ITEM_REGEX = Regex("""(?m)^\s*\d+\.\s+.*$""")

    fun highlight(sourceText: String): AnnotatedString {
        return AnnotatedString.Builder(sourceText).apply {
            applyPattern(this, sourceText, CODE_FENCE_REGEX, SpanStyle(color = CODE_COLOR))
            applyPattern(this, sourceText, HEADER_REGEX, SpanStyle(color = HEADER_COLOR, fontWeight = FontWeight.Bold))
            applyPattern(this, sourceText, HORIZONTAL_RULE_REGEX, SpanStyle(color = HR_COLOR, fontWeight = FontWeight.Bold))
            applyPattern(this, sourceText, BLOCKQUOTE_REGEX, SpanStyle(color = QUOTE_COLOR, fontStyle = FontStyle.Italic, fontFamily = FontFamily.Serif))
            applyPattern(this, sourceText, ORDERED_LIST_ITEM_REGEX, SpanStyle(color = LIST_COLOR))
            applyPattern(this, sourceText, LIST_ITEM_REGEX, SpanStyle(color = LIST_COLOR))
            applyPattern(this, sourceText, BOLD_REGEX, SpanStyle(color = BOLD_COLOR, fontWeight = FontWeight.Bold))
            applyPattern(this, sourceText, ITALIC_REGEX, SpanStyle(fontStyle = FontStyle.Italic, fontFamily = FontFamily.Serif))
            applyPattern(this, sourceText, STRIKETHROUGH_REGEX, SpanStyle(color = STRIKETHROUGH_COLOR, textDecoration = TextDecoration.LineThrough))
            applyPattern(this, sourceText, INLINE_CODE_REGEX, SpanStyle(color = CODE_COLOR))
            applyPattern(this, sourceText, LINK_REGEX, SpanStyle(color = LINK_COLOR, fontWeight = FontWeight.Medium))
        }.toAnnotatedString()
    }

    private fun applyPattern(
        builder: AnnotatedString.Builder,
        text: String,
        regex: Regex,
        style: SpanStyle
    ) {
        for (match in regex.findAll(text)) {
            builder.addStyle(style, match.range.first, match.range.last + 1)
        }
    }
}
