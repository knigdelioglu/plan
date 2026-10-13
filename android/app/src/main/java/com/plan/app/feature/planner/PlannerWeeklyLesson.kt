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
internal fun WeeklyLessonPlanView(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek?,
    activeWeekNumber: Int,
    showWeekSummary: Boolean
) {
    val colors = ArcTheme.colors
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()

    if (activeWeek == null) {
        val loading by viewModel.planLoading.collectAsState()
        val error by viewModel.planError.collectAsState()
        if (loading) {
            Text("Yıllık plan yükleniyor…", color = ArcTheme.colors.textSecondary)
            return
        }
        if (error != null) {
            Text(error.orEmpty(), color = ArcTheme.colors.danger)
            return
        }
        val plan = viewModel.classrooms.collectAsState().value
            .firstOrNull { it.id == activeClassroomId }
        ArcEmptyStatesBlock.PlanNotAvailable(
            gradeLevel = plan?.gradeLevel ?: 11,
            courseName = plan?.courseName ?: "Türk Dili ve Edebiyatı"
        )
        return
    }

    val currentPlan by viewModel.currentPlan.collectAsState()
    val totalWeeks = currentPlan?.weeks?.maxOfOrNull { it.weekNumber } ?: activeWeekNumber

    Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)) {
        if (showWeekSummary) {
            // On a phone or when the sidebar is hidden, keep the same navigation.
            ArcCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(ArcSpacing.space3),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        WeekHeaderLabel(activeWeek, colors.foreground, colors.accent)
                    }
                    WeekNavigationButtons(viewModel, activeWeekNumber, totalWeeks, compact = false)
                }
            }
            WeekSpecialDaysNotice(activeWeek)
        }

        // Lesson Parts Breakdown (Supports 3+2 distribution)
        Text(
            text = "Ders Dağılımı (${activeWeek.lessons.size} Bölüm • Toplam ${activeWeek.totalHours} Saat)",
            style = ArcTheme.typography.titleMedium,
            color = colors.foreground
        )

        activeWeek.lessons.forEach { lesson ->
            val isCompleted = progress.any {
                it.classroomId == activeClassroomId &&
                    it.weekNumber == activeWeek.weekNumber &&
                    it.lessonIndex == lesson.lessonIndex && it.isCompleted
            }

            ArcSwipeActions(
                // Swipe left = completed; swipe right = not completed. Repeat gestures are safe.
                onSwipeLeft = { viewModel.setLessonProgress(activeWeek.weekNumber, lesson.lessonIndex, true) },
                onSwipeRight = { viewModel.setLessonProgress(activeWeek.weekNumber, lesson.lessonIndex, false) },
                onSwipeLeftLabel = "Dersi işlendi olarak işaretle",
                onSwipeRightLabel = "Dersin işlendi işaretini kaldır"
            ) {
                ArcCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isCompleted) colors.success.copy(alpha = 0.5f) else colors.border
                ) {
                    var lessonDetailsVisible by rememberSaveable(activeClassroomId, activeWeek.weekNumber, lesson.lessonIndex) { mutableStateOf(false) }
                    Column(modifier = Modifier.padding(ArcSpacing.space3)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ArcCheckbox(
                                    checked = isCompleted,
                                    onCheckedChange = { next ->
                                        viewModel.setLessonProgress(activeWeek.weekNumber, lesson.lessonIndex, next)
                                    },
                                    modifier = Modifier.semantics {
                                        contentDescription = "${activeWeek.weekNumber}. hafta, ${lesson.lessonIndex}. bölüm işlendi"
                                    }
                                )
                                Column(
                                    modifier = Modifier.clickable {
                                        lessonDetailsVisible = !lessonDetailsVisible
                                    }
                                ) {
                                    Text(
                                        text = "${lesson.lessonIndex}. Bölüm: ${lesson.learningArea ?: "Ders Alanı"}",
                                        style = ArcTheme.typography.titleMedium,
                                        color = if (isCompleted) colors.textSecondary else colors.foreground
                                    )
                                    Text(
                                        text = buildString {
                                            append("${lesson.hours} Ders Saati")
                                            localizedHoursSourceStatus(lesson.hoursSourceStatus)?.let {
                                                append(" · ")
                                                append(it)
                                            }
                                        },
                                        style = ArcTheme.typography.labelSmall,
                                        color = colors.textMuted
                                    )
                                }
                            }

                            if (isCompleted) {
                                ArcBadge(text = "Tamamlandı", variant = ArcBadgeVariant.SUCCESS)
                            }
                        }

                        // Outcomes are essential lesson information and do not depend on details.
                        if (lesson.learningOutcomes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(ArcSpacing.space2))
                            LessonLearningOutcomes(outcomes = lesson.learningOutcomes)
                        }
                        if (lesson.textsAndMaterials.isNotEmpty() || lesson.processComponents.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                ArcButton(
                                    onClick = { lessonDetailsVisible = !lessonDetailsVisible },
                                    variant = ArcButtonVariant.SUBTLE
                                ) {
                                    Text(if (lessonDetailsVisible) "Gizle" else "Ayrıntılar")
                                }
                            }
                        }
                        if (lessonDetailsVisible) {
                            Spacer(modifier = Modifier.height(ArcSpacing.space3))

                        // Topic and Texts
                        if (lesson.textsAndMaterials.isNotEmpty()) {
                            Text(
                                text = "İncelenecek Metinler & Materyaller:",
                                style = ArcTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            lesson.textsAndMaterials.forEach { mat ->
                                Text(
                                    text = "• $mat",
                                    style = ArcTheme.typography.bodySmall,
                                    color = colors.foreground
                                )
                            }
                            Spacer(modifier = Modifier.height(ArcSpacing.space2))
                        }

                        // Process Components (SDB / D / OB)
                        if (lesson.processComponents.isNotEmpty()) {
                            Text(
                                text = "Süreç Bileşenleri (Maarif Modeli):",
                                style = ArcTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            lesson.processComponents.forEach { component ->
                                ArcExpandableCard(
                                    title = component.title.ifBlank { component.mainCode },
                                    subtitle = component.mainCode
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        component.subComponents.forEach { sub ->
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                ArcBadge(
                                                    text = sub.code.ifBlank { sub.subCode },
                                                    variant = ArcBadgeVariant.NEUTRAL
                                                )
                                                Column {
                                                    Text(
                                                        sub.title,
                                                        style = ArcTheme.typography.bodySmall,
                                                        color = colors.foreground
                                                    )
                                                    sub.items.forEach { item ->
                                                        Text(
                                                            "• " + item,
                                                            style = ArcTheme.typography.bodySmall,
                                                            color = colors.textSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        } // lessonDetailsVisible
                    }
                }
            }
        }

        // Supporting context and note editor are available on demand, not always visible.
        key(activeClassroomId, activeWeek.weekNumber) {
            ArcExpandableCard(
                title = "Öğretmen notları ve pedagojik açıklamalar",
                subtitle = "Notlar, değerlendirme ve farklılaştırma"
            ) {
                TeacherNotesAndPedagogicalSection(viewModel = viewModel, activeWeek = activeWeek)
            }
        }
    }
}

@Composable
private fun WeekHeaderLabel(
    week: PlanWeek,
    titleColor: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "${week.weekNumber}. Hafta · ${week.dateRangeRaw}",
            style = ArcTheme.typography.titleLarge,
            color = titleColor
        )
        week.theme?.let { theme ->
            Text(
                text = "${theme.themeCode}: ${theme.themeName}",
                style = ArcTheme.typography.bodyMedium,
                color = accentColor
            )
        }
    }
}

@Composable
private fun WeekNavigationButtons(
    viewModel: PlannerViewModel,
    weekNumber: Int,
    lastWeekNumber: Int,
    @Suppress("UNUSED_PARAMETER") compact: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(
            onClick = { viewModel.selectWeek(weekNumber - 1) },
            enabled = weekNumber > 1,
            modifier = Modifier.size(40.dp)
        ) { Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki hafta") }
        IconButton(
            onClick = { viewModel.selectWeek(weekNumber + 1) },
            enabled = weekNumber < lastWeekNumber,
            modifier = Modifier.size(40.dp)
        ) { Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki hafta") }
    }
}


private fun localizedHoursSourceStatus(status: String?): String? = when (status) {
    "verified_from_cell" -> "Kaynak hücresinden doğrulandı"
    "verified_from_cell_distribution" -> "Kaynak hücre dağılımından doğrulandı"
    "assumed_from_curriculum_standard" -> "Ders çizelgesi standardına göre varsayıldı"
    else -> null
}

