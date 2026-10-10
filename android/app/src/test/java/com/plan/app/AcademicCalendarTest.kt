package com.plan.app

import com.plan.app.core.data.PlanRepository
import com.plan.app.core.model.AcademicCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.LocalDate
import java.time.YearMonth

class AcademicCalendarTest {
    private fun plan() = PlanRepository(
        plansBaseDir = File("../../plans").takeIf { it.exists() } ?: File("plans")
    ).loadAnnualPlan("Anadolu Lisesi")

    @Test
    fun actualWeekdaysAndFirstTeachingWeekAreAligned() {
        val plan = plan()
        val days = AcademicCalendar.monthDays(YearMonth.of(2026, 9), plan.weeks, plan.academicCalendarBreaks)
        assertEquals(LocalDate.of(2026, 8, 31), days.first().date)
        assertEquals(1, days.first { it.date == LocalDate.of(2026, 9, 14) }.teachingWeekNumber)
        assertNull(days.first { it.date == LocalDate.of(2026, 9, 7) }.teachingWeekNumber)
        assertTrue(days.first { it.date == LocalDate.of(2026, 9, 14) }.hasSpecialEvent)
    }

    @Test
    fun novemberBreakDoesNotCreateTeachingWeek() {
        val plan = plan()
        val days = AcademicCalendar.monthDays(YearMonth.of(2026, 11), plan.weeks, plan.academicCalendarBreaks)
        val vacation = days.first { it.date == LocalDate.of(2026, 11, 16) }
        assertTrue(vacation.isAcademicBreak)
        assertNull(vacation.teachingWeekNumber)
        assertEquals(10, days.first { it.date == LocalDate.of(2026, 11, 23) }.teachingWeekNumber)
    }

    @Test
    fun marchBreakDoesNotShiftTeachingWeeks() {
        val plan = plan()
        val days = AcademicCalendar.monthDays(YearMonth.of(2027, 3), plan.weeks, plan.academicCalendarBreaks)
        assertTrue(days.first { it.date == LocalDate.of(2027, 3, 12) }.isAcademicBreak)
        assertNull(days.first { it.date == LocalDate.of(2027, 3, 12) }.teachingWeekNumber)
        assertFalse(days.first { it.date == LocalDate.of(2027, 3, 15) }.isAcademicBreak)
    }
}
