package com.plan.app.feature.planner

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.blocks.ArcCommandPalette
import com.plan.app.core.design.blocks.ArcEmptyStatesBlock
import com.plan.app.core.design.blocks.ArcNotificationCenter
import com.plan.app.core.design.blocks.ArcStatsBand
import com.plan.app.core.design.blocks.CommandItem
import com.plan.app.core.design.components.advanced.ArcActivityHeatmap
import com.plan.app.core.design.components.advanced.ArcInput
import com.plan.app.core.design.components.advanced.ArcSwipeActions
import com.plan.app.core.design.components.core.ArcActionButton
import com.plan.app.core.design.components.core.ArcAlert
import com.plan.app.core.design.components.core.ArcAlertVariant
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.components.core.ArcCalendar
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.components.core.ArcCheckbox
import com.plan.app.core.design.components.core.ArcDialog
import com.plan.app.core.design.components.core.ArcDrawer
import com.plan.app.core.design.components.core.ArcExpandableCard
import com.plan.app.core.design.components.core.ArcResizablePanels
import com.plan.app.core.design.components.core.ArcSegmentedControl
import com.plan.app.core.design.components.core.ArcSelect
import com.plan.app.core.design.components.extended.ArcBarChart
import com.plan.app.core.design.components.extended.ArcDonutChart
import com.plan.app.core.design.components.extended.ArcLineChart
import com.plan.app.core.design.components.extended.ArcRichTextEditor
import com.plan.app.core.design.components.extended.ArcUsageMeter
import com.plan.app.core.design.components.extended.ArcUserMenu
import com.plan.app.core.design.components.extended.BarChartItem
import com.plan.app.core.design.components.extended.DonutSlice
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.knigdelioglu.arc.compose.display.ArcProgress
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.NoteType
import com.plan.app.core.model.PlanLesson
import com.plan.app.core.model.PlanWeek
import java.time.LocalDate
import com.plan.app.core.model.PlannerViewMode

@Composable
internal fun AnalyticsAndChartsView(
    viewModel: PlannerViewModel,
    currentPlan: com.plan.app.core.model.AnnualPlan?
) {
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()
    val today by viewModel.currentDate.collectAsState()
    val completedHours = currentPlan?.weeks.orEmpty().sumOf { w ->
        w.lessons.filter { l ->
            progress.any {
                it.classroomId == activeClassroomId && it.weekNumber == w.weekNumber &&
                    it.lessonIndex == l.lessonIndex && it.isCompleted
            }
        }.sumOf { it.hours }
    }
    val timeline = PlanTimeline.progress(currentPlan?.weeks.orEmpty(), today)
    val verifiedHours = currentPlan?.metadata?.verifiedLessonHours
    val assumedHours = currentPlan?.metadata?.assumedLessonHours
    val annualHours = currentPlan?.metadata?.totalAnnualHoursAssumed
        ?: currentPlan?.metadata?.totalAnnualHours
        ?: currentPlan?.weeks?.sumOf { it.totalHours } ?: 0

    // Never display hardcoded curriculum statistics or a fabricated verification split.
    val chartColors = listOf(
        Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFF10B981),
        Color(0xFFF59E0B), Color(0xFF8B5CF6)
    )
    val themes = currentPlan?.themes.orEmpty()
    val remainingHours = (annualHours - themes.sumOf { it.totalHours }).coerceAtLeast(0)
    val themeSlices = themes.mapIndexed { index, theme ->
        DonutSlice(theme.themeName, theme.totalHours.toFloat(), chartColors[index % chartColors.size])
    } + if (remainingHours > 0) listOf(
        DonutSlice("Diğer / Sosyal Etkinlik", remainingHours.toFloat(), chartColors[4])
    ) else emptyList()

    val barItems = themes.mapIndexed { index, theme ->
        BarChartItem(theme.themeCode, theme.totalHours.toFloat(), chartColors[index % chartColors.size])
    } + if (remainingHours > 0) listOf(
        BarChartItem("Diğer", remainingHours.toFloat(), chartColors[4])
    ) else emptyList()

    val cumulativePoints = currentPlan?.weeks.orEmpty()
        .runningFold(0) { total, week ->
            total + week.lessons.filter { lesson ->
                progress.any {
                    it.classroomId == activeClassroomId && it.weekNumber == week.weekNumber &&
                        it.lessonIndex == lesson.lessonIndex && it.isCompleted
                }
            }.sumOf { it.hours }
        }.drop(1).map { it.toFloat() }

    val completedWeeks = currentPlan?.weeks.orEmpty().filter { week ->
        week.lessons.isNotEmpty() && week.lessons.all { lesson ->
            progress.any {
                it.classroomId == activeClassroomId && it.weekNumber == week.weekNumber &&
                    it.lessonIndex == lesson.lessonIndex && it.isCompleted
            }
        }
    }.map { it.weekNumber }.toSet()
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)) {
        Text(
            text = "Takvime göre ilerleme · ${timeline.percent}% (${timeline.elapsedHours} / ${timeline.totalHours} saat)",
            style = ArcTheme.typography.titleMedium,
            color = ArcTheme.colors.foreground
        )
        ArcProgress(progress = timeline.fraction, height = 8.dp)
        Text(
            text = "Geçen öğretim haftaları otomatik sayılır. İşlendi olarak işaretlenen saatler aşağıda ayrı gösterilir.",
            style = ArcTheme.typography.bodySmall,
            color = ArcTheme.colors.textSecondary
        )
        Text(
            text = "İşaretlenmiş dersler · $completedHours / $annualHours saat",
            style = ArcTheme.typography.titleMedium,
            color = ArcTheme.colors.foreground
        )
        ArcUsageMeter(
            completedHours = completedHours,
            verifiedHours = verifiedHours,
            assumedHours = assumedHours,
            annualHours = annualHours
        )

        Text(
            "Plana göre tema dağılımı",
            style = ArcTheme.typography.titleMedium,
            color = ArcTheme.colors.foreground
        )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth < 620.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)) {
                    ArcDonutChart(slices = themeSlices)
                    ArcBarChart(items = barItems)
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ArcSpacing.space4)
                ) {
                    Box(modifier = Modifier.weight(1f)) { ArcDonutChart(slices = themeSlices) }
                    Box(modifier = Modifier.weight(1f)) { ArcBarChart(items = barItems) }
                }
            }
        }

        Text(
            "Tamamlanan ders saatleri",
            style = ArcTheme.typography.titleMedium,
            color = ArcTheme.colors.foreground
        )
        ArcLineChart(dataPoints = cumulativePoints)
        ArcActivityHeatmap(
            totalWeeks = currentPlan?.weeks?.size ?: 0,
            completedWeeks = completedWeeks,
            currentWeek = activeWeekNumber,
            onSelectWeek = { week ->
                viewModel.selectWeek(week)
                viewModel.selectViewMode(PlannerViewMode.WEEK)
            }
        )
    }
}
