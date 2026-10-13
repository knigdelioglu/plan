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
internal fun MonthlyOverviewView(viewModel: PlannerViewModel) {
    val currentPlan by viewModel.currentPlan.collectAsState()
    val weeks = currentPlan?.weeks ?: emptyList()
    val colors = ArcTheme.colors

    val months = listOf("EYLÜL", "EKİM", "KASIM", "ARALIK", "OCAK", "ŞUBAT", "MART", "NİSAN", "MAYIS", "HAZİRAN")

    Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)) {
        Text(text = "Aylık Müfredat Takvimi", style = ArcTheme.typography.titleLarge, color = colors.foreground)

        months.forEach { monthName ->
            val monthWeeks = weeks.filter { it.month.equals(monthName, ignoreCase = true) }
            if (monthWeeks.isNotEmpty()) {
                ArcCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(ArcSpacing.space4)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = monthName, style = ArcTheme.typography.titleMedium, color = colors.foreground)
                            ArcBadge(text = "${monthWeeks.size} Hafta", variant = ArcBadgeVariant.NEUTRAL)
                        }
                        Spacer(modifier = Modifier.height(ArcSpacing.space2))

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            monthWeeks.forEach { w ->
                                Surface(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clickable {
                                            viewModel.selectWeek(w.weekNumber)
                                            viewModel.selectViewMode(PlannerViewMode.WEEK)
                                        },
                                    shape = ArcRadius.sm,
                                    color = colors.surfaceMuted,
                                    border = BorderStroke(1.dp, colors.border)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "${w.weekNumber}. Hafta", style = ArcTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Text(text = w.dateRangeRaw, style = ArcTheme.typography.bodySmall, fontSize = 11.sp, color = colors.textSecondary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "${w.lessons.size} ders bölümü", style = ArcTheme.typography.bodySmall, color = colors.textMuted)
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

@Composable
internal fun ThematicCurriculumView(currentPlan: com.plan.app.core.model.AnnualPlan?) {
    val themes = currentPlan?.themes ?: emptyList()
    val colors = ArcTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)) {
        Text(text = "Tematik Öğretim Programı (4 Tema)", style = ArcTheme.typography.titleLarge, color = colors.foreground)

        themes.forEach { theme ->
            var assessmentExpanded by rememberSaveable(theme.themeCode) { mutableStateOf(false) }
            val assessmentText = theme.assessmentAndEvaluation?.text?.trim().orEmpty()
            val assessmentPreview = assessmentText.take(200).let { excerpt ->
                if (assessmentText.length > excerpt.length) {
                    "${excerpt.substringBeforeLast(' ', excerpt).trimEnd()}…"
                } else {
                    assessmentText
                }
            }
            ArcCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(ArcSpacing.space5)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "${theme.themeCode}: ${theme.themeName}", style = ArcTheme.typography.titleLarge, color = colors.foreground)
                            Text(text = "${theme.weekRange.firstOrNull() ?: 1} – ${theme.weekRange.lastOrNull() ?: 9}. Haftalar Arası", style = ArcTheme.typography.bodySmall, color = colors.textSecondary)
                        }
                        ArcBadge(text = "${theme.totalHours} Saat", variant = ArcBadgeVariant.ACCENT)
                    }

                    Spacer(modifier = Modifier.height(ArcSpacing.space3))

                    Text(
                        text = "Ölçme ve Değerlendirme Yaklaşımı:",
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            assessmentText.isBlank() -> "Ölçme değerlendirme bilgisi kaynakta belirtilmemiştir."
                            assessmentExpanded || assessmentText.length <= 200 -> assessmentText
                            else -> assessmentPreview
                        },
                        style = ArcTheme.typography.bodySmall,
                        color = colors.foreground
                    )
                    if (assessmentText.length > 200) {
                        ArcButton(
                            onClick = { assessmentExpanded = !assessmentExpanded },
                            variant = ArcButtonVariant.SUBTLE
                        ) {
                            Text(
                                if (assessmentExpanded) "Daha az göster" else "Metnin tamamını gör",
                                style = ArcTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

