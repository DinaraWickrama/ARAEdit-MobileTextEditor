package com.example.mobiletexteditor.highlighting

/**
 * Central property-name list for CSS highlighting — the "keyword file"
 * equivalent for stylesheets, mirroring KotlinKeywords. Not exhaustive of
 * every CSS property that exists; covers the common ones so highlighting
 * is useful without the list becoming unwieldy.
 */
object CssKeywords {
    val COMMON_PROPERTIES = setOf(
        "color", "background", "background-color", "background-image",
        "margin", "margin-top", "margin-bottom", "margin-left", "margin-right",
        "padding", "padding-top", "padding-bottom", "padding-left", "padding-right",
        "border", "border-radius", "border-color", "border-width", "border-style",
        "width", "height", "min-width", "min-height", "max-width", "max-height",
        "display", "position", "top", "bottom", "left", "right", "z-index",
        "flex", "flex-direction", "flex-wrap", "justify-content", "align-items",
        "grid", "grid-template-columns", "grid-template-rows", "gap",
        "font", "font-size", "font-weight", "font-family", "font-style",
        "line-height", "letter-spacing", "text-align", "text-decoration",
        "text-transform", "white-space", "overflow", "overflow-x", "overflow-y",
        "box-shadow", "opacity", "transform", "transition", "animation",
        "cursor", "visibility", "float", "clear", "content", "box-sizing"
    )
}
