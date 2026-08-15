package com.example.mobiletexteditor.highlighting

/**
 * Central tag list for HTML highlighting — the "keyword file" equivalent for
 * markup, mirroring KotlinKeywords. Kept separate from the tokenizer so it's
 * trivial to extend without touching HtmlHighlighter's regex/parsing logic.
 */
object HtmlKeywords {
    val COMMON_TAGS = setOf(
        "html", "head", "body", "title", "meta", "link", "style", "script",
        "div", "span", "p", "a", "img", "ul", "ol", "li", "table", "tr", "td",
        "th", "thead", "tbody", "form", "input", "button", "label", "select",
        "option", "textarea", "header", "footer", "nav", "main", "section",
        "article", "aside", "h1", "h2", "h3", "h4", "h5", "h6", "br", "hr",
        "strong", "em", "b", "i", "u", "small", "code", "pre", "blockquote",
        "iframe", "video", "audio", "source", "canvas", "svg", "path",
        "figure", "figcaption", "picture", "template", "noscript"
    )

    // Tags with no closing tag / children — useful if you later want to warn
    // on "</img>" etc, not required for basic keyword highlighting.
    val VOID_ELEMENTS = setOf(
        "area", "base", "br", "col", "embed", "hr", "img", "input", "link",
        "meta", "param", "source", "track", "wbr"
    )
}
