package com.plan.app.core.design.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.AcademicCalendar
import com.plan.app.core.model.AcademicCalendarBreak
import com.plan.app.core.model.PlanWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Real month grid with teaching weeks and school breaks from the annual-plan JSON. */
@Composable
fun ArcCalendar(
    selectedWeek: Int?,
    onSelectWeek: (Int) -> Unit,
    planWeeks: List<PlanWeek>,
    academicBreaks: List<AcademicCalendarBreak>,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    val firstDate = remember(planWeeks) {
        planWeeks.firstOrNull()?.startDate?.let(LocalDate::parse)
    }
    val lastDate = remember(planWeeks) {
        planWeeks.lastOrNull()?.endDate?.let(LocalDate::parse)
    }
    val selectedStart = remember(planWeeks, selectedWeek) {
        planWeeks.firstOrNull { it.weekNumber == selectedWeek }?.startDate?.let(LocalDate::parse)
    }
    val planKey = firstDate?.toString()
    val selectedKey = selectedStart?.toString()
    var monthValue by rememberSaveable(planKey) {
        mutableStateOf(YearMonth.from(selectedStart ?: firstDate ?: LocalDate.now()).toString())
    }
    // Remember which week was synchronized. A rotation must not force a browsed
    // calendar month back to the selected week's month.
    var syncedSelection by rememberSaveable(planKey) {
        mutableStateOf(selectedKey)
    }
    LaunchedEffect(selectedKey) {
        if (selectedKey != null && selectedKey != syncedSelection) {
            monthValue = YearMonth.from(selectedStart).toString()
            syncedSelection = selectedKey
        }
    }
    val displayedMonth = YearMonth.parse(monthValue)
    val earliestMonth = firstDate?.let(YearMonth::from)
    val latestMonth = lastDate?.let(YearMonth::from)
    val days = remember(displayedMonth, planWeeks, academicBreaks) {
        AcademicCalendar.monthDays(displayedMonth, planWeeks, academicBreaks)
    }
    val monthTitle = displayedMonth.format(
        DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("tr-TR"))
    ).replaceFirstChar { it.uppercase() }

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space3)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(monthTitle, style = ArcTheme.typography.titleMedium, color = colors.foreground)
                Row {
                    IconButton(
                        onClick = { monthValue = displayedMonth.minusMonths(1).toString() },
                        enabled = earliestMonth != null && displayedMonth > earliestMonth,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki ay")
                    }
                    IconButton(
                        onClick = { monthValue = displayedMonth.plusMonths(1).toString() },
                        enabled = latestMonth != null && displayedMonth < latestMonth,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki ay")
                    }
                }
            }
            Spacer(Modifier.height(ArcSpacing.space2))
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz").forEach { name ->
                    Text(
                        name,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textSecondary
                    )
                }
            }
            days.chunked(7).forEach { weekDays ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    weekDays.forEach { day ->
                        val enabled = day.inDisplayedMonth && day.teachingWeekNumber != null
                        val selected = enabled && selectedWeek == day.teachingWeekNumber
                        val spokenDate = day.date.format(
                            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("tr-TR"))
                        )
                        val spokenLabel = buildString {
                            append(spokenDate)
                            if (day.isAcademicBreak) {
                                append(", ara tatil")
                                day.breakName?.takeIf { it.isNotBlank() }?.let { append(", $it") }
                            }
                            day.teachingWeekNumber?.let { append(", $it. öğretim haftası") }
                            if (day.hasSpecialEvent) append(", özel gün")
                            if (selected) append(", seçili")
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .semantics {
                                    contentDescription = spokenLabel
                                    this.selected = selected
                                }
                                .clickable(
                                    enabled = enabled,
                                    role = Role.Button
                                ) {
                                    day.teachingWeekNumber?.let(onSelectWeek)
                                },
                            shape = ArcRadius.sm,
                            color = if (selected) colors.surfaceMuted else Color.Transparent,
                            border = if (selected) BorderStroke(1.dp, colors.accent) else null
                        ) {
                            Column(
                                modifier = Modifier.height(48.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = day.date.dayOfMonth.toString(),
                                    style = ArcTheme.typography.bodySmall,
                                    color = when {
                                        !day.inDisplayedMonth -> colors.textMuted.copy(alpha = 0.4f)
                                        day.isAcademicBreak -> colors.warning
                                        enabled -> colors.foreground
                                        else -> colors.textMuted
                                    },
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (day.hasSpecialEvent || day.isAcademicBreak) {
                                    Box(
                                        modifier = Modifier.size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (day.isAcademicBreak) colors.warning else colors.accent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
