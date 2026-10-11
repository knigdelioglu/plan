package com.plan.app.core.design.components.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.knigdelioglu.arc.compose.calendar.ArcScheduleBreak
import com.knigdelioglu.arc.compose.calendar.ArcScheduleCalendar
import com.knigdelioglu.arc.compose.calendar.ArcScheduleWeek
import com.plan.app.core.model.AcademicCalendarBreak
import com.plan.app.core.model.PlanWeek
import java.time.LocalDate

/** Adapts source-plan dates to the shared calendar's UI models without changing plan data. */
@Composable
fun ArcCalendar(
    selectedWeek: Int?,
    onSelectWeek: (Int) -> Unit,
    planWeeks: List<PlanWeek>,
    academicBreaks: List<AcademicCalendarBreak>,
    modifier: Modifier = Modifier
) {
    val scheduleWeeks = planWeeks.mapNotNull { week ->
        val start = week.startDate.toLocalDateOrNull() ?: return@mapNotNull null
        val end = week.endDate.toLocalDateOrNull() ?: return@mapNotNull null
        ArcScheduleWeek(
            number = week.weekNumber,
            startDate = start,
            endDate = end,
            hasSpecialEvent = week.specialDaysAndWeeks?.present == true
        )
    }
    val scheduleBreaks = academicBreaks.mapNotNull { schoolBreak ->
        val start = schoolBreak.startDate.toLocalDateOrNull() ?: return@mapNotNull null
        val end = schoolBreak.endDate.toLocalDateOrNull() ?: return@mapNotNull null
        ArcScheduleBreak(
            label = schoolBreak.breakName,
            startDate = start,
            endDate = end
        )
    }

    ArcScheduleCalendar(
        selectedWeek = selectedWeek,
        onSelectWeek = onSelectWeek,
        weeks = scheduleWeeks,
        breaks = scheduleBreaks,
        modifier = modifier
    )
}

private fun String.toLocalDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(this) }.getOrNull()
