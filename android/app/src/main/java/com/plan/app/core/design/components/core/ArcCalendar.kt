package com.plan.app.core.design.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Calendar Component (30 / 70)
 * Native tablet-friendly calendar grid with week jumping and event dots.
 */
data class CalendarDay(
    val dayOfMonth: Int,
    val weekNumber: Int?,
    val isCurrentMonth: Boolean = true,
    val isToday: Boolean = false,
    val hasSpecialEvent: Boolean = false,
    val isAcademicBreak: Boolean = false,
    val isCompleted: Boolean = false
)

@Composable
fun ArcCalendar(
    selectedWeek: Int?,
    onSelectWeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
    monthTitle: String = "Eylül 2026",
    onPrevMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    weeksWithEvents: Set<Int> = emptySet(),
    weeksWithBreaks: Set<Int> = emptySet()
) {
    val colors = ArcTheme.colors
    val daysOfWeek = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = monthTitle,
                    style = ArcTheme.typography.titleMedium,
                    color = colors.foreground
                )
                Row {
                    IconButton(onClick = onPrevMonth, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki", tint = colors.textSecondary)
                    }
                    IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki", tint = colors.textSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space2))

            // Day names row
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space2))

            // 5 Weeks Grid Sample
            for (weekIndex in 1..5) {
                val weekNum = weekIndex
                val isSelectedWeek = selectedWeek == weekNum
                val hasEvent = weeksWithEvents.contains(weekNum)
                val hasBreak = weeksWithBreaks.contains(weekNum)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable { onSelectWeek(weekNum) },
                    shape = ArcRadius.sm,
                    color = if (isSelectedWeek) colors.surfaceMuted else Color.Transparent,
                    border = if (isSelectedWeek) BorderStroke(1.dp, colors.accent) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (d in 1..7) {
                            val dayNumber = (weekIndex - 1) * 7 + d
                            val isSchoolDay = d <= 5

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (dayNumber <= 31) dayNumber.toString() else "",
                                        style = ArcTheme.typography.bodySmall,
                                        color = if (isSchoolDay) colors.foreground else colors.textMuted,
                                        fontWeight = if (isSelectedWeek) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isSchoolDay && (hasEvent || hasBreak)) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (hasBreak) colors.warning else colors.accent)
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
}
