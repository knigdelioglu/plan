package com.plan.app

import com.plan.app.feature.planner.NoteDraftBuffer
import com.plan.app.feature.planner.NoteDraftKey
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Deterministic concurrency tests: no emulator or arbitrary timing sleeps. */
class P2NoteDraftConcurrencyTest {
    @Test
    fun slowDiskWriteDoesNotBlockTypingAndDoesNotLoseNewerText() {
        val enteredWriter = CountDownLatch(1)
        val releaseWriter = CountDownLatch(1)
        val latest = mutableListOf<String>()
        val key = NoteDraftKey("11-A", 5)
        val buffer = NoteDraftBuffer { _, text ->
            if (text == "first") {
                enteredWriter.countDown()
                check(releaseWriter.await(5, TimeUnit.SECONDS))
            }
            synchronized(latest) { latest += text }
        }
        buffer.stage(key, "first")
        val pool = Executors.newFixedThreadPool(2)
        try {
            val flush = pool.submit<Int> { buffer.flush() }
            assertTrue("writer must begin", enteredWriter.await(5, TimeUnit.SECONDS))

            // The writer is blocked; a new keystroke must still finish without
            // waiting for storage, even under high filesystem latency.
            val typed = pool.submit<Boolean> { buffer.stage(key, "second") }
            assertTrue("typing must not wait on IO", typed.get(1, TimeUnit.SECONDS))
            assertEquals("second", buffer.currentText(key, "disk text"))
            releaseWriter.countDown()
            assertEquals(1, flush.get(5, TimeUnit.SECONDS))
            assertTrue(buffer.hasPending())
            assertEquals(1, buffer.flush())
            assertEquals(listOf("first", "second"), latest)
            assertFalse(buffer.hasPending())
        } finally {
            releaseWriter.countDown()
            pool.shutdownNow()
        }
    }

    @Test
    fun simultaneousFlushesDoNotPersistNewValueBeforeOlderValue() {
        val enteredWriter = CountDownLatch(1)
        val releaseWriter = CountDownLatch(1)
        val written = mutableListOf<String>()
        val key = NoteDraftKey("11-C", 9)
        val buffer = NoteDraftBuffer { _, text ->
            if (text == "before") {
                enteredWriter.countDown()
                check(releaseWriter.await(5, TimeUnit.SECONDS))
            }
            synchronized(written) { written += text }
        }
        buffer.stage(key, "before")
        val pool = Executors.newFixedThreadPool(2)
        try {
            val first = pool.submit<Int> { buffer.flush() }
            assertTrue(enteredWriter.await(5, TimeUnit.SECONDS))
            buffer.stage(key, "after")
            val second = pool.submit<Int> { buffer.flush() }
            releaseWriter.countDown()
            assertEquals(1, first.get(5, TimeUnit.SECONDS))
            assertEquals(1, second.get(5, TimeUnit.SECONDS))
            assertEquals(listOf("before", "after"), written)
            assertFalse(buffer.hasPending())
        } finally {
            releaseWriter.countDown()
            pool.shutdownNow()
        }
    }

    @Test
    fun failedLongWriteKeepsHundredKilobyteDraftAndLaterRetryWorks() {
        val key = NoteDraftKey("11-B", 11)
        val largeNote = "Edebiyat atölyesi\n".repeat(6_000)
        var fail = true
        var saved = ""
        val buffer = NoteDraftBuffer { _, text ->
            if (fail) throw IOException("storage unavailable")
            saved = text
        }
        buffer.stage(key, largeNote)
        assertThrows(IOException::class.java) { buffer.flush() }
        assertEquals(largeNote, buffer.currentText(key, ""))
        fail = false
        assertEquals(1, buffer.flush())
        assertEquals(largeNote, saved)
        assertFalse(buffer.hasPending())
    }
}
