package com.plan.app.feature.planner

/**
 * One pending edit per classroom/week. Keeps the last draft in RAM until a
 * successful durable write. Never removes a draft when the writer throws.
 *
 * This is intentionally independent of Android, so loss/retry paths can be
 * verified deterministically by JVM unit tests.
 */
data class NoteDraftKey(val classroomId: String, val weekNumber: Int)

class NoteDraftBuffer(private val persist: (NoteDraftKey, String) -> Unit) {
    private val pending = LinkedHashMap<NoteDraftKey, String>()

    @Synchronized
    fun stage(key: NoteDraftKey, text: String): Boolean {
        if (pending[key] == text) return false
        pending[key] = text
        return true
    }

    @Synchronized
    fun currentText(key: NoteDraftKey, persistedText: String): String =
        pending[key] ?: persistedText

    @Synchronized
    fun hasPending(): Boolean = pending.isNotEmpty()

    /**
     * The buffer lock serializes writers and new edits. If one write fails,
     * that entry and all later entries remain pending for the next retry.
     */
    @Synchronized
    fun flush(): Int {
        var written = 0
        val iterator = pending.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            persist(entry.key, entry.value)
            iterator.remove()
            written++
        }
        return written
    }
}
