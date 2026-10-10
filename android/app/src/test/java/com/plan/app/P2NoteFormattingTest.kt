package com.plan.app

import com.plan.app.core.design.components.extended.NoteFormat
import com.plan.app.core.design.components.extended.formatTeacherNote
import org.junit.Assert.assertEquals
import org.junit.Test

class P2NoteFormattingTest {
    @Test fun boldWrapsSelectionWithoutFabricatedWords() {
        val result = formatTeacherNote("Ders notu", 5, 9, NoteFormat.BOLD)
        assertEquals("Ders **notu**", result.text)
        assertEquals(7, result.selectionStart)
        assertEquals(11, result.selectionEnd)
    }

    @Test fun boldAtCaretLeavesCursorBetweenMarkers() {
        val result = formatTeacherNote("Ders", 4, 4, NoteFormat.BOLD)
        assertEquals("Ders****", result.text)
        assertEquals(6, result.selectionStart)
        assertEquals(6, result.selectionEnd)
    }

    @Test fun italicFormatsSelectionWithoutLosingSurroundingText() {
        val result = formatTeacherNote("Metin inceleme", 6, 14, NoteFormat.ITALIC)
        assertEquals("Metin *inceleme*", result.text)
    }

    @Test fun multilineListPrefixesEachLineExactlyOnce() {
        val result = formatTeacherNote("bir\niki", 0, 7, NoteFormat.LIST)
        assertEquals("- bir\n- iki", result.text)
        val repeated = formatTeacherNote(result.text, 0, result.text.length, NoteFormat.LIST)
        assertEquals(result.text, repeated.text)
    }

    @Test fun quoteAddsPrefixToCurrentLineAtCaret() {
        val result = formatTeacherNote("Başlık\nAlıntı", 9, 9, NoteFormat.QUOTE)
        assertEquals("Başlık\n> Alıntı", result.text)
    }

    @Test fun staleSelectionFromWindowChangeIsClamped() {
        val result = formatTeacherNote("Yeni", 99, 103, NoteFormat.BOLD)
        assertEquals("Yeni****", result.text)
        assertEquals(6, result.selectionStart)
    }
}
