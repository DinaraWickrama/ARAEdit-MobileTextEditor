package com.example.mobiletexteditor.highlighting

/**
 * Central keyword file for Kotlin highlighting, per the "ideally with a keyword file"
 * requirement. Kept as a plain data object so it's trivial to extend or externalize
 * to a JSON asset later without touching the tokenizer.
 */
object KotlinKeywords {
    val HARD_KEYWORDS = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
        "if", "in", "interface", "is", "null", "object", "package", "return",
        "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
        "var", "when", "while"
    )

    val SOFT_KEYWORDS = setOf(
        "by", "catch", "constructor", "delegate", "dynamic", "field", "file",
        "finally", "get", "import", "init", "param", "property", "receiver",
        "set", "setparam", "where", "actual", "abstract", "annotation",
        "companion", "const", "crossinline", "data", "enum", "expect",
        "external", "final", "infix", "inline", "inner", "internal", "lateinit",
        "noinline", "open", "operator", "out", "override", "private",
        "protected", "public", "reified", "sealed", "suspend", "tailrec", "vararg"
    )

    val ALL: Set<String> = HARD_KEYWORDS + SOFT_KEYWORDS
}
