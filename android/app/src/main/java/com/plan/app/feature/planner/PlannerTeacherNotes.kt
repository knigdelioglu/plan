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
internal fun TeacherNotesAndPedagogicalSection(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek
) {
    val colors = ArcTheme.colors
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val restoreGeneration by viewModel.restoreGeneration.collectAsState()
    val notes by viewModel.notesList.collectAsState()
    val existingNote = notes.firstOrNull {
        it.classroomId == activeClassroomId && it.weekNumber == activeWeek.weekNumber && it.lessonIndex == null
    }
    // Never reuse a note editor state between two classrooms at the same week.
    var noteText by remember(activeClassroomId, activeWeek.weekNumber, restoreGeneration) {
        mutableStateOf(viewModel.noteTextFor(
            activeClassroomId, activeWeek.weekNumber, existingNote?.text ?: ""
        ))
    }
    val noteSaveState by viewModel.noteSaveState.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space3)) {
        Text(
            text = "Pedagojik Açıklamalar ve Öğretmen Çalışma Alanı",
            style = ArcTheme.typography.titleMedium,
            color = colors.foreground
        )

        // Expandable Pedagogical Context Cards
        val ped = activeWeek.pedagogicalContext
        if (ped != null) {
            ArcExpandableCard(
                title = "Ölçme ve Değerlendirme Süreci",
                subtitle = "Metin tahlili ve edebiyat atölyesi değerlendirme yöntemleri"
            ) {
                Text(
                    text = ped.assessmentAndEvaluation?.text ?: "Ölçme değerlendirme bilgisi kaynakta belirtilmemiştir.",
                    style = ArcTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            ArcExpandableCard(
                title = "Disiplinlerarası Pedagojik Bağlam (SDB • Değerler • Okuryazarlık)",
                subtitle = "Sosyal-Duygusal Beceriler, Değerler ve Okuryazarlık kodları"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "SDB Becerileri: ${ped.socialEmotionalLearningSkills?.codes?.joinToString(", ") ?: "Yok"}", style = ArcTheme.typography.bodySmall)
                    Text(text = "Değerler: ${ped.values?.codes?.joinToString(", ") ?: "Yok"}", style = ArcTheme.typography.bodySmall)
                    Text(text = "Okuryazarlık: ${ped.literacySkills?.codes?.joinToString(", ") ?: "Yok"}", style = ArcTheme.typography.bodySmall)
                }
            }

            ArcExpandableCard(
                title = "Farklılaştırma & Okul Temelli Planlama",
                subtitle = "Zenginleştirme, destekleme ve zümre kararları"
            ) {
                Text(
                    text = ped.differentiation?.text ?: "Farklılaştırma açıklaması kaynakta tanımlanmıştır.",
                    style = ArcTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // Rich Text Notes
        key(activeClassroomId, activeWeek.weekNumber, restoreGeneration) {
            ArcRichTextEditor(
                value = noteText,
                onValueChange = {
                    noteText = it
                    viewModel.updateTeacherNoteDraft(activeClassroomId, activeWeek.weekNumber, it)
                }
            )
        }
        Text(
            text = when (noteSaveState) {
                NoteSaveState.SAVED -> "Notlar kaydedildi"
                NoteSaveState.PENDING -> "Notlar kaydediliyor…"
                NoteSaveState.FAILED -> "Kayıt başarısız. Not taslağı korunuyor; tekrar denemek için dokunun."
            },
            modifier = Modifier
                .then(if (noteSaveState == NoteSaveState.FAILED) {
                    Modifier
                        .heightIn(min = 48.dp)
                        .clickable(onClickLabel = "Notları kaydetmeyi yeniden dene") {
                            viewModel.flushPendingTeacherNotes()
                        }
                } else Modifier),
            style = ArcTheme.typography.labelSmall,
            color = if (noteSaveState == NoteSaveState.FAILED) colors.danger else colors.textSecondary
        )

        // Signature UI intentionally removed for this personal weekly planner.
    }
}

