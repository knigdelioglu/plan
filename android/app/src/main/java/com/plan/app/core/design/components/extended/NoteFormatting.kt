package com.plan.app.core.design.components.extended

/** The editor stores plain Markdown, never fabricated example text. */
enum class NoteFormat { BOLD, ITALIC, LIST, QUOTE }

data class FormattedNote(
    val text: String,
    val selectionStart: Int,
    val selectionEnd: Int
)

/**
 * Pure formatting transformation. Handles a selection or an empty caret,
 * and clamps stale selection offsets after Android window recreation.
 */
fun formatTeacherNote(
    text: String,
    selectionStart: Int,
    selectionEnd: Int,
    format: NoteFormat
): FormattedNote {
    val start = minOf(selectionStart, selectionEnd).coerceIn(0, text.length)
    val end = maxOf(selectionStart, selectionEnd).coerceIn(0, text.length)
    if (format == NoteFormat.BOLD || format == NoteFormat.ITALIC) {
        val marker = if (format == NoteFormat.BOLD) "**" else "*"
        val replacement = marker + text.substring(start, end) + marker
        val edited = text.replaceRange(start, end, replacement)
        return if (start == end) {
            val caret = start + marker.length
            FormattedNote(edited, caret, caret)
        } else {
            FormattedNote(edited, start + marker.length, end + marker.length)
        }
    }

    val prefix = if (format == NoteFormat.LIST) "- " else "> "
    val lineStart = text.lastIndexOf('\n', start - 1) + 1
    val lineEnd = if (end > start && text[end - 1] == '\n') end - 1 else end
    val finalLineEnd = text.indexOf('\n', lineEnd).let { if (it < 0) text.length else it }
    val section = text.substring(lineStart, finalLineEnd)
    val replacement = section.split("\n").joinToString("\n") {
        if (it.startsWith(prefix)) it else prefix + it
    }
    val edited = text.replaceRange(lineStart, finalLineEnd, replacement)
    val newCaret = (lineStart + replacement.length).coerceAtMost(edited.length)
    return FormattedNote(edited, newCaret, newCaret)
}
