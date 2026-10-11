package com.plan.app.feature.planner

/**
 * Latest pending teacher note per classroom/week.
 *
 * Staging must never wait for a slow AtomicFile disk write. A dedicated write
 * lock serializes flushes while the small state lock protects only map access.
 * New edits arriving during a write are retained and can be flushed again.
 */
data class NoteDraftKey(val classroomId: String, val weekNumber: Int)

class NoteDraftBuffer(private val persist: (NoteDraftKey, String) -> Unit) {
    private val pendingLock = Any()
    private val writeLock = Any()
    private val pending = LinkedHashMap<NoteDraftKey, String>()

    fun stage(key: NoteDraftKey, text: String): Boolean = synchronized(pendingLock) {
        if (pending[key] == text) return@synchronized false
        pending[key] = text
        true
    }

    fun currentText(key: NoteDraftKey, persistedText: String): String =
        synchronized(pendingLock) { pending[key] ?: persistedText }

    fun hasPending(): Boolean = synchronized(pendingLock) { pending.isNotEmpty() }

    /**
     * A flush commits the entries that existed when it acquired the writer lock.
     * It never removes an edit made while its older value was being written.
     * The caller retries if hasPending() remains true.
     */
    fun flush(): Int = synchronized(writeLock) {
        val batch = synchronized(pendingLock) { pending.toList() }
        var written = 0
        for ((key, value) in batch) {
            persist(key, value) // May block on storage: NEVER hold pendingLock here.
            synchronized(pendingLock) {
                if (pending[key] == value) pending.remove(key)
            }
            written++
        }
        written
    }
}
