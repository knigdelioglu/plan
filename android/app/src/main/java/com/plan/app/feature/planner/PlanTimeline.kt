package com.plan.app.feature.planner

import com.plan.app.core.model.PlanWeek
import java.time.LocalDate

/** Calendar state is independent of teacher confirmation checkboxes. */
internal data class PlanTimelineProgress(
    val elapsedHours: Int,
    val totalHours: Int
) {
    val fraction: Float
        get() = if (totalHours > 0) (elapsedHours.toFloat() / totalHours).coerceIn(0f, 1f) else 0f
    val percent: Int get() = (fraction * 100).toInt()
}

internal object PlanTimeline {
    /**
     * Source weeks are authoritative. On an instructional day show that week; in a
     * gap (weekend/holiday) show the next teaching week. Before/after the plan,
     * clamp to the first/last known week. Malformed dates are ignored.
     */
    fun relevantWeek(weeks: List<PlanWeek>, today: LocalDate): Int? {
        val ordered = weeks.mapNotNull { week ->
            val start = runCatching { LocalDate.parse(week.startDate) }.getOrNull()
            val end = runCatching { LocalDate.parse(week.endDate) }.getOrNull()
            if (start == null || end == null || end.isBefore(start)) null
            else Triple(start, end, week.weekNumber)
        }.sortedBy { it.first }
        if (ordered.isEmpty()) return null
        return ordered.firstOrNull { !today.isBefore(it.first) && !today.isAfter(it.second) }?.third
            ?: ordered.firstOrNull { today.isBefore(it.first) }?.third
            ?: ordered.last().third
    }

    /** Only entirely elapsed teaching weeks count automatically; vacations do not. */
    fun progress(weeks: List<PlanWeek>, today: LocalDate): PlanTimelineProgress {
        val total = weeks.sumOf { it.totalHours.coerceAtLeast(0) }
        val elapsed = weeks.sumOf { week ->
            val end = runCatching { LocalDate.parse(week.endDate) }.getOrNull()
            if (end != null && end.isBefore(today)) week.totalHours.coerceAtLeast(0) else 0
        }
        return PlanTimelineProgress(elapsedHours = elapsed.coerceAtMost(total), totalHours = total)
    }
}
