package com.plan.app

import com.plan.app.feature.planner.NoteDraftBuffer
import com.plan.app.feature.planner.NoteDraftKey
import org.junit.Assert.*
import org.junit.Test

class P2NoteDraftBufferTest {
    @Test
    fun typingOneHundredCharactersDoesNotWriteOneHundredSnapshots() {
        val writes = mutableListOf<String>()
        val key = NoteDraftKey("11-A", 3)
        val buffer = NoteDraftBuffer { _, value -> writes += value }
        repeat(100) { index ->
            buffer.stage(key, "x".repeat(index + 1))
        }
        assertTrue(buffer.hasPending())
        assertEquals("x".repeat(100), buffer.currentText(key, ""))
        assertTrue("Typing must not cause disk writes", writes.isEmpty())
        assertEquals(1, buffer.flush())
        assertEquals(listOf("x".repeat(100)), writes)
        assertFalse(buffer.hasPending())
        assertEquals(0, buffer.flush())
    }

    @Test
    fun classesAndWeeksNeverOverwriteEachOther() {
        val saved = mutableMapOf<NoteDraftKey, String>()
        val buffer = NoteDraftBuffer { key, text -> saved[key] = text }
        val a = NoteDraftKey("11-A", 5)
        val b = NoteDraftKey("11-B", 5)
        val c = NoteDraftKey("11-A", 6)
        buffer.stage(a, "A sınıfı")
        buffer.stage(b, "B sınıfı")
        buffer.stage(c, "Sonraki hafta")
        assertEquals("A sınıfı", buffer.currentText(a, "eski not"))
        assertEquals("B sınıfı", buffer.currentText(b, ""))
        assertEquals("Sonraki hafta", buffer.currentText(c, ""))
        assertEquals(3, buffer.flush())
        assertEquals(mapOf(a to "A sınıfı", b to "B sınıfı", c to "Sonraki hafta"), saved)
    }

    @Test
    fun failingAtomicWriterRetainsUnsavedDraftForRetry() {
        var fail = true
        val persisted = mutableListOf<String>()
        val key = NoteDraftKey("11-A", 7)
        val buffer = NoteDraftBuffer { _, text ->
            if (fail) throw java.io.IOException("disk full")
            persisted += text
        }
        buffer.stage(key, "Kayıp olmaması gereken not")
        assertThrows(java.io.IOException::class.java) { buffer.flush() }
        assertTrue(buffer.hasPending())
        assertEquals("Kayıp olmaması gereken not", buffer.currentText(key, "old"))
        fail = false
        assertEquals(1, buffer.flush())
        assertEquals(listOf("Kayıp olmaması gereken not"), persisted)
        assertFalse(buffer.hasPending())
    }

    @Test
    fun interruptedBatchRetriesOnlyUncommittedEntries() {
        val saved = mutableListOf<String>()
        var failSecond = true
        val buffer = NoteDraftBuffer { _, text ->
            if (text == "second" && failSecond) throw java.io.IOException("temporary")
            saved += text
        }
        buffer.stage(NoteDraftKey("A", 1), "first")
        buffer.stage(NoteDraftKey("A", 2), "second")
        assertThrows(java.io.IOException::class.java) { buffer.flush() }
        assertEquals(listOf("first"), saved)
        assertTrue(buffer.hasPending())
        // The failed entry remains; the successful first one is not repeated.
        assertEquals("second", buffer.currentText(NoteDraftKey("A", 2), "old"))
        failSecond = false
        assertEquals(1, buffer.flush())
        assertEquals(listOf("first", "second"), saved)
        assertFalse(buffer.hasPending())
    }

    @Test
    fun repeatedSameTextDoesNotQueueAnotherWrite() {
        val buffer = NoteDraftBuffer { _, _ -> }
        val key = NoteDraftKey("11-C", 11)
        assertTrue(buffer.stage(key, "metin"))
        assertFalse(buffer.stage(key, "metin"))
        assertEquals(1, buffer.flush())
    }
}
