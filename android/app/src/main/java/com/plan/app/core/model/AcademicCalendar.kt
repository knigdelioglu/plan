package com.plan.app.core.model

import java.time.LocalDate
import java.time.YearMonth

/**
 * Teaching week numbers come from annual-plan.json. Vacation dates never
 * create or renumber teaching weeks.
 */
data class PlanCalendarDay(
    val date: LocalDate,
    val inDisplayedMonth: Boolean,
    val teachingWeekNumber: Int?,
    val isAcademicBreak: Boolean,
    val breakName: String?,
    val hasSpecialEvent: Boolean
)

object AcademicCalendar {
    fun monthDays(
        month: YearMonth,
        weeks: List<PlanWeek>,
        breaks: List<AcademicCalendarBreak>
    ): List<PlanCalendarDay> {
        val first = month.atDay(1)
        val monday = first.minusDays((first.dayOfWeek.value - 1).toLong())
        val requiredCells = first.dayOfWeek.value - 1 + month.lengthOfMonth()
        val cellCount = ((requiredCells + 6) / 7) * 7
        val teaching = weeks.map { Triple(it, LocalDate.parse(it.startDate), LocalDate.parse(it.endDate)) }
        val vacations = breaks.map { Triple(it, LocalDate.parse(it.startDate), LocalDate.parse(it.endDate)) }
        return (0 until cellCount).map { offset ->
            val date = monday.plusDays(offset.toLong())
            val vacation = vacations.firstOrNull { (_, start, end) -> !date.isBefore(start) && !date.isAfter(end) }
            val week = if (vacation == null) {
                teaching.firstOrNull { (_, start, end) -> !date.isBefore(start) && !date.isAfter(end) }?.first
            } else null
            PlanCalendarDay(
                date = date,
                inDisplayedMonth = YearMonth.from(date) == month,
                teachingWeekNumber = week?.weekNumber,
                isAcademicBreak = vacation != null,
                breakName = vacation?.first?.breakName,
                hasSpecialEvent = week?.specialDaysAndWeeks?.present == true
            )
        }
    }
}
