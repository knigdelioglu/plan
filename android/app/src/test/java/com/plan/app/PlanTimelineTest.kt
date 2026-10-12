package com.plan.app

import com.plan.app.core.model.PlanWeek
import com.plan.app.feature.planner.PlanTimeline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class PlanTimelineTest {
    private val weeks = listOf(
        week(1, "2026-09-14", "2026-09-18", 5),
        week(2, "2026-09-21", "2026-09-25", 3),
        week(3, "2026-10-05", "2026-10-09", 5)
    )

    private fun week(n: Int, start: String, end: String, hours: Int) =
        PlanWeek(n, "EYLÜL", "$start – $end", start, end, hours)

    @Test fun choosesActualWeekOnStartupRatherThanStoredFirstWeek() {
        assertEquals(2, PlanTimeline.relevantWeek(weeks, LocalDate.parse("2026-09-23")))
        assertEquals(1, PlanTimeline.relevantWeek(weeks, LocalDate.parse("2026-09-01")))
        assertEquals(3, PlanTimeline.relevantWeek(weeks, LocalDate.parse("2026-11-10")))
    }

    @Test fun gapsAndWeekendsSelectNextTeachingWeekWithoutInventingVacationWeeks() {
        assertEquals(2, PlanTimeline.relevantWeek(weeks, LocalDate.parse("2026-09-19")))
        assertEquals(3, PlanTimeline.relevantWeek(weeks, LocalDate.parse("2026-09-26")))
        assertEquals(3, PlanTimeline.relevantWeek(weeks, LocalDate.parse("2026-10-05")))
    }

    @Test fun progressAccruesAfterCompletedCalendarWeeksWithoutAnyCheckmarks() {
        assertEquals(0, PlanTimeline.progress(weeks, LocalDate.parse("2026-09-14")).elapsedHours)
        assertEquals(0, PlanTimeline.progress(weeks, LocalDate.parse("2026-09-18")).elapsedHours)
        assertEquals(5, PlanTimeline.progress(weeks, LocalDate.parse("2026-09-19")).elapsedHours)
        assertEquals(8, PlanTimeline.progress(weeks, LocalDate.parse("2026-09-26")).elapsedHours)
        assertEquals(8, PlanTimeline.progress(weeks, LocalDate.parse("2026-10-05")).elapsedHours)
        assertEquals(13, PlanTimeline.progress(weeks, LocalDate.parse("2026-10-10")).elapsedHours)
        assertEquals(100, PlanTimeline.progress(weeks, LocalDate.parse("2026-10-10")).percent)
    }

    @Test fun emptyAndBadDatePlansFailSafely() {
        assertNull(PlanTimeline.relevantWeek(emptyList(), LocalDate.now()))
        assertEquals(0, PlanTimeline.progress(emptyList(), LocalDate.now()).percent)
        assertNull(PlanTimeline.relevantWeek(
            listOf(week(1, "invalid", "2026-01-02", 5)), LocalDate.now()
        ))
        assertEquals(0, PlanTimeline.progress(
            listOf(week(1, "invalid", "invalid", 5)), LocalDate.now()
        ).elapsedHours)
    }
}
