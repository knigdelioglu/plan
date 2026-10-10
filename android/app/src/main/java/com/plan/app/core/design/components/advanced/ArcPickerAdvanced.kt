package com.plan.app.core.design.components.advanced

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
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
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Date Picker Component (38 / 70 - B.2)
 */
@Composable
fun ArcDatePicker(
    selectedDate: String, // e.g. "2026-09-15"
    onDateSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    var currentDay by remember { mutableIntStateOf(15) }

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tarih Seç: $selectedDate",
                    style = ArcTheme.typography.titleMedium,
                    color = colors.foreground
                )
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            // 7x5 Days sample grid
            for (row in 0..4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 1..7) {
                        val day = row * 7 + col
                        if (day <= 30) {
                            val isSelected = day == currentDay
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) colors.accent else Color.Transparent)
                                    .clickable {
                                        currentDay = day
                                        val dayStr = if (day < 10) "0$day" else "$day"
                                        onDateSelect("2026-09-$dayStr")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = ArcTheme.typography.bodySmall,
                                    color = if (isSelected) colors.accentForeground else colors.foreground,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Arc Date Range Picker Component (39 / 70 - B.3)
 */
@Composable
fun ArcDateRangePicker(
    startDate: String,
    endDate: String,
    onRangeSelect: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    var startDay by remember { mutableIntStateOf(16) }
    var endDay by remember { mutableIntStateOf(20) }

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Text(
                text = "Tarih Aralığı: $startDate — $endDate",
                style = ArcTheme.typography.titleMedium,
                color = colors.foreground
            )

            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 14..22) {
                    val isInRange = day in startDay..endDay
                    val isBoundary = day == startDay || day == endDay

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(ArcRadius.sm)
                            .background(
                                if (isBoundary) colors.accent
                                else if (isInRange) colors.accentSubtle
                                else Color.Transparent
                            )
                            .clickable {
                                if (day < startDay) startDay = day
                                else endDay = day
                                onRangeSelect("2026-11-$startDay", "2026-11-$endDay")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            style = ArcTheme.typography.bodySmall,
                            color = if (isBoundary) colors.accentForeground else colors.foreground,
                            fontWeight = if (isInRange) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
