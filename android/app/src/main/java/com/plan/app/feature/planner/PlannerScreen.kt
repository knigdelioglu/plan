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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
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
fun PlannerScreen(viewModel: PlannerViewModel) {
    val classrooms by viewModel.classrooms.collectAsState()
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val recoveryRequired by viewModel.recoveryRequired.collectAsState()
    val storageError by viewModel.storageError.collectAsState()
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
    val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsState()
    val fontScalePercent by viewModel.fontScalePercent.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val filter by viewModel.filter.collectAsState()

    val currentClass = classrooms.firstOrNull { it.id == activeClassroomId } ?: classrooms.firstOrNull()
    // Derive from collected Compose state so the lesson content follows week navigation.
    val activeWeek = currentPlan?.weeks?.firstOrNull { it.weekNumber == activeWeekNumber }
    val colors = ArcTheme.colors
    if (recoveryRequired) {
        PlannerRecoveryScreen(
            message = storageError,
            onImportBackup = viewModel::importBackupJson
        )
        return
    }

    var showNewClassDialog by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showSettings) { showSettings = false }
    // Hoisted above the compact/two-pane branch: DeX resize does not reset it.
    var sidebarRatio by rememberSaveable { mutableFloatStateOf(0.29f) }
    var sidebarCollapsed by rememberSaveable { mutableStateOf(false) }
    var newClassName by remember { mutableStateOf("") }
    val availablePlans by viewModel.availablePlans.collectAsState()
    var selectedPlanPath by remember { mutableStateOf<String?>(null) }
    val selectedPlan = availablePlans.firstOrNull { it.pathSegment == selectedPlanPath }
        ?: availablePlans.firstOrNull()

    // Source-backed quick commands. Removed views never appear in the palette.
    val commandItems = remember(currentPlan) {
        currentPlan?.weeks.orEmpty().map { week ->
            CommandItem(
                id = "week-${week.weekNumber}",
                title = "${week.weekNumber}. Hafta — ${week.lessons.firstOrNull()?.topicTitle.orEmpty()}",
                category = "Haftalar",
                icon = Icons.Default.Event
            ) {
                viewModel.selectWeek(week.weekNumber)
                viewModel.selectViewMode(PlannerViewMode.WEEK)
            }
        } + listOf(
            CommandItem("view-week", "Haftalık plan", "Görünümler", icon = Icons.Default.Event) {
                viewModel.selectViewMode(PlannerViewMode.WEEK)
            },
            CommandItem("view-month", "Aylık takvim", "Görünümler", icon = Icons.Default.Event) {
                viewModel.selectViewMode(PlannerViewMode.MONTH)
            },
            CommandItem("view-themes", "Temalar", "Görünümler", icon = Icons.Default.Description) {
                viewModel.selectViewMode(PlannerViewMode.THEME)
            },
            CommandItem("view-progress", "İlerleme", "Görünümler", icon = Icons.Default.Check) {
                viewModel.selectViewMode(PlannerViewMode.ANALYTICS)
            }
        )
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(colors.background).safeDrawingPadding().imePadding()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    (event.isCtrlPressed || event.isMetaPressed) && event.key == Key.K
                ) {
                    viewModel.setCommandPaletteOpen(true)
                    true
                } else false
            }
            .focusable()
    ) {
        if (showSettings) {
            PlannerSettingsScreen(
                fontScalePercent = fontScalePercent,
                onFontScaleChange = viewModel::setFontScalePercent,
                isDarkMode = isDarkMode,
                onToggleDarkMode = viewModel::toggleDarkMode,
                onExportBackup = viewModel::exportBackupJson,
                onImportBackup = viewModel::importBackupJson,
                onBack = { showSettings = false }
            )
        } else {
        // A narrow week strip and a toggleable monthly calendar allow smaller sidebars.
        val isTabletLandscape =
            PlannerWindowPolicy.modeFor(maxWidth.value) == PlannerWindowMode.TWO_PANE

        if (isTabletLandscape && !sidebarCollapsed) {
            ArcResizablePanels(
                modifier = Modifier.testTag("planner-two-pane"),
                initialSplitRatio = 0.29f,
                controlledSplitRatio = sidebarRatio,
                onSplitRatioChange = { sidebarRatio = it },
                minFirstPanelWidth = PlannerWindowPolicy.MinSidebarDp.dp,
                minSecondPanelWidth = PlannerWindowPolicy.MinWorkspaceDp.dp,
                firstPanel = {
                    LeftSidebar(
                        viewModel = viewModel,
                        currentClass = currentClass,
                        classrooms = classrooms,
                        onOpenNewClassDialog = { showNewClassDialog = true },
                        onCollapse = { sidebarCollapsed = true }
                    )
                },
                secondPanel = {
                    ScaledPlannerWorkspace(fontScalePercent) {
                        CenterWorkspace(
                            viewModel = viewModel,
                            activeWeek = activeWeek,
                            currentClass = currentClass,
                            viewMode = viewMode,
                            onOpenSettings = { if (viewModel.flushPendingTeacherNotes()) showSettings = true },
                            onToggleSidebar = { sidebarCollapsed = !sidebarCollapsed },
                            sidebarVisible = true
                        )
                    }
                }
            )
        } else if (isTabletLandscape) {
            Box(modifier = Modifier.fillMaxSize().testTag("planner-two-pane")) {
                ScaledPlannerWorkspace(fontScalePercent) {
                    CenterWorkspace(
                        viewModel = viewModel,
                        activeWeek = activeWeek,
                        currentClass = currentClass,
                        viewMode = viewMode,
                        onOpenSettings = { if (viewModel.flushPendingTeacherNotes()) showSettings = true },
                        onToggleSidebar = { sidebarCollapsed = false },
                        sidebarVisible = false
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().testTag("planner-compact")) {
                LeftSidebarHeader(
                    viewModel = viewModel,
                    currentClass = currentClass,
                    classrooms = classrooms,
                    onOpenNewClassDialog = { showNewClassDialog = true }
                )
                Box(modifier = Modifier.weight(1f)) {
                    ScaledPlannerWorkspace(fontScalePercent) {
                        CenterWorkspace(
                            viewModel = viewModel,
                            activeWeek = activeWeek,
                            currentClass = currentClass,
                            viewMode = viewMode,
                            onOpenSettings = { if (viewModel.flushPendingTeacherNotes()) showSettings = true }
                        )
                    }
                }
            }
        }

        // Overlays & Dialogs
        ArcCommandPalette(
            isOpen = isCommandPaletteOpen,
            onDismiss = { viewModel.setCommandPaletteOpen(false) },
            commands = commandItems
        )

        ArcDrawer(
            isOpen = isNotificationCenterOpen,
            onClose = { viewModel.setNotificationCenterOpen(false) },
            width = 380.dp
        ) {
            ArcNotificationCenter(
                notifications = notifications,
                onDismissNotification = { viewModel.dismissNotification(it) },
                onClearAll = { viewModel.clearAllNotifications() },
                onClose = { viewModel.setNotificationCenterOpen(false) }
            )
        }

        if (showNewClassDialog) {
            ArcDialog(
                isOpen = showNewClassDialog,
                onDismissRequest = { showNewClassDialog = false },
                title = "Yeni Sınıf Ekle",
                confirmButton = {
                    ArcButton(onClick = {
                        if (newClassName.isNotBlank() && selectedPlan != null) {
                            viewModel.addClassroom(newClassName, selectedPlan)
                            newClassName = ""
                            showNewClassDialog = false
                        }
                    }) {
                        Text("Oluştur")
                    }
                },
                dismissButton = {
                    ArcButton(onClick = { showNewClassDialog = false }, variant = ArcButtonVariant.SUBTLE) {
                        Text("İptal")
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ArcInput(
                        value = newClassName,
                        onValueChange = { newClassName = it },
                        label = "Sınıf Adı",
                        placeholder = "örn: 11-C veya 11-Fen B"
                    )
                    Text(text = "Yıllık Plan", style = ArcTheme.typography.labelSmall, color = colors.textSecondary)
                    ArcSelect(
                        selectedItem = selectedPlan,
                        items = availablePlans,
                        onSelect = { selectedPlanPath = it.pathSegment },
                        itemLabel = { "${it.academicYear} · ${it.gradeLevel}. sınıf · ${it.schoolType}" },
                        placeholder = "Mevcut plan bulunamadı"
                    )
                }
            }
        }
        } // planner view
    }
}

@Composable
private fun LeftSidebar(
    viewModel: PlannerViewModel,
    currentClass: Classroom?,
    classrooms: List<Classroom>,
    onOpenNewClassDialog: () -> Unit,
    onCollapse: () -> Unit
) {
    val colors = ArcTheme.colors
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    var fullCalendarVisible by rememberSaveable { mutableStateOf(false) }
    val selectedWeek = currentPlan?.weeks?.firstOrNull { it.weekNumber == activeWeekNumber }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(ArcSpacing.space4)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)
        ) {
            // User & Class Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArcUserMenu(
                    userName = currentClass?.name ?: "Sınıf Seçiniz",
                    userRole = "${currentClass?.schoolType ?: "Anadolu Lisesi"} • 2026-2027",
                    menuOptions = classrooms.map { cls ->
                        Pair(cls.name) { viewModel.selectClassroom(cls.id) }
                    } + Pair("+ Yeni Sınıf") { onOpenNewClassDialog() }
                )

                // New class is already available in the user menu; no duplicate action.
                IconButton(onClick = onCollapse, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Sol paneli gizle", tint = colors.textSecondary)
                }
            }

            // The monthly library calendar is preserved, but opened only on demand.
            ArcCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(ArcSpacing.space3),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedWeek?.dateRangeRaw ?: "Hafta seçin",
                            style = ArcTheme.typography.titleMedium,
                            color = colors.foreground
                        )
                        IconButton(
                            modifier = Modifier.size(40.dp),
                            onClick = { fullCalendarVisible = !fullCalendarVisible }
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = if (fullCalendarVisible) "Takvimi daralt" else "Aylık takvimi aç",
                                tint = colors.textSecondary
                            )
                        }
                    }
                    val startDate = remember(selectedWeek?.startDate) {
                        selectedWeek?.startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                    }
                    if (startDate != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val weekdayLabels = listOf("Pzt", "Sal", "Çar", "Per", "Cum")
                            weekdayLabels.forEachIndexed { index, label ->
                                val date = startDate.plusDays(index.toLong())
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = com.plan.app.core.design.theme.ArcRadius.md,
                                    color = if (date == LocalDate.now()) colors.surfaceMuted else colors.surface
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(label, style = ArcTheme.typography.labelSmall, color = colors.textMuted)
                                        Text(date.dayOfMonth.toString(), style = ArcTheme.typography.bodyMedium, color = colors.foreground)
                                    }
                                }
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        IconButton(
                            onClick = { viewModel.selectWeek(activeWeekNumber - 1) },
                            enabled = activeWeekNumber > 1,
                            modifier = Modifier.size(40.dp)
                        ) { Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki hafta") }
                        Text(
                            text = "${activeWeekNumber}. / ${currentPlan?.weeks?.size ?: 0} hafta",
                            style = ArcTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        IconButton(
                            onClick = { viewModel.selectWeek(activeWeekNumber + 1) },
                            enabled = activeWeekNumber < (currentPlan?.weeks?.maxOfOrNull { it.weekNumber } ?: 0),
                            modifier = Modifier.size(40.dp)
                        ) { Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki hafta") }
                    }
                }
            }
            if (fullCalendarVisible) {
                ArcCalendar(
                    selectedWeek = activeWeekNumber,
                    onSelectWeek = {
                        viewModel.selectWeek(it)
                        viewModel.selectViewMode(PlannerViewMode.WEEK)
                    },
                    planWeeks = currentPlan?.weeks.orEmpty(),
                    academicBreaks = currentPlan?.academicCalendarBreaks.orEmpty()
                )
            }

            // Weekly content replaces the expansive curriculum hierarchy.
            SelectedWeekSidebarCard(selectedWeek)
            WeekSpecialDaysNotice(selectedWeek)

        }
    }
}

@Composable
private fun SelectedWeekSidebarCard(week: PlanWeek?) {
    val colors = ArcTheme.colors
    ArcCard(modifier = Modifier.fillMaxWidth().testTag("sidebar-week-card")) {
        Column(
            modifier = Modifier.padding(ArcSpacing.space3),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("İlgili Hafta", style = ArcTheme.typography.labelSmall, color = colors.textSecondary)
            if (week == null) {
                Text("Yıllık plan bulunamadı", color = colors.textSecondary)
            } else {
                Text(
                    "${week.weekNumber}. Hafta · ${week.dateRangeRaw}",
                    style = ArcTheme.typography.titleMedium,
                    color = colors.foreground
                )
                week.theme?.let { theme ->
                    Text(
                        "${theme.themeCode}: ${theme.themeName}",
                        style = ArcTheme.typography.bodyMedium,
                        color = colors.foreground
                    )
                }
                Text(
                    "Toplam ${week.totalHours} ders saati · ${week.lessons.size} bölüm",
                    style = ArcTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
                week.lessons.forEach { lesson ->
                    Text(
                        "${lesson.lessonIndex}. Bölüm: ${lesson.learningArea ?: lesson.topicTitle ?: "Ders"} (${lesson.hours} saat)",
                        style = ArcTheme.typography.bodySmall,
                        color = colors.foreground
                    )
                }
            }
        }
    }
}

@Composable
internal fun WeekSpecialDaysNotice(week: PlanWeek?) {
    val specialDay = week?.specialDaysAndWeeks?.takeIf { it.present } ?: return
    ArcAlert(
        title = "Özel Gün ve Hafta: ${specialDay.raw ?: specialDay.text ?: specialDay.events.joinToString(", ") { it.name }}",
        message = specialDay.schedulingNote
            ?: specialDay.events.firstOrNull()?.calendarNote
            ?: "Kaynak yıllık planda bu hafta için belirtilmiştir.",
        variant = ArcAlertVariant.INFO,
        modifier = Modifier.fillMaxWidth().testTag("week-special-days")
    )
}

@Composable
private fun LeftSidebarHeader(
    viewModel: PlannerViewModel,
    currentClass: Classroom?,
    classrooms: List<Classroom>,
    onOpenNewClassDialog: () -> Unit
) {
    val colors = ArcTheme.colors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArcUserMenu(
                userName = currentClass?.name ?: "Sınıf Seçiniz",
                userRole = currentClass?.schoolType ?: "Anadolu Lisesi",
                menuOptions = classrooms.map { cls ->
                    Pair(cls.name) { viewModel.selectClassroom(cls.id) }
                } + Pair("+ Yeni Sınıf") { onOpenNewClassDialog() }
            )

            // Theme control is in the compact workspace toolbar; do not repeat it here.
        }
    }
}

@Composable
private fun ScaledPlannerWorkspace(
    fontScalePercent: Int,
    content: @Composable () -> Unit
) {
    val systemDensity = LocalDensity.current
    val workspaceDensity = remember(systemDensity, fontScalePercent) {
        Density(
            density = systemDensity.density,
            fontScale = systemDensity.fontScale * (fontScalePercent / 100f)
        )
    }
    CompositionLocalProvider(LocalDensity provides workspaceDensity, content = content)
}

@Composable
private fun CenterWorkspace(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek?,
    currentClass: Classroom?,
    viewMode: PlannerViewMode,
    onOpenSettings: () -> Unit,
    onToggleSidebar: (() -> Unit)? = null,
    sidebarVisible: Boolean = true
) {
    val colors = ArcTheme.colors
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val today by viewModel.currentDate.collectAsState()
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val searchResults = remember(currentPlan, filter.query) { viewModel.searchResults() }
    val completedHours = currentPlan?.weeks.orEmpty().sumOf { week ->
        week.lessons.filter { lesson ->
            progress.any {
                it.classroomId == activeClassroomId &&
                    it.weekNumber == week.weekNumber &&
                    it.lessonIndex == lesson.lessonIndex && it.isCompleted
            }
        }.sumOf { it.hours }
    }

    var showSearch by rememberSaveable { mutableStateOf(false) }
    var showStats by rememberSaveable { mutableStateOf(false) }
    var showViews by rememberSaveable { mutableStateOf(false) }
    val timeline = PlanTimeline.progress(currentPlan?.weeks.orEmpty(), today)
    val completion = timeline.fraction
    val storageError by viewModel.storageError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ArcSpacing.space3)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(ArcSpacing.space3)
    ) {
        // Compact product header: class identity lives in the selector, not a second banner.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (onToggleSidebar != null && !sidebarVisible) currentClass?.name ?: viewMode.title
                        else "${viewMode.title} plan",
                    style = ArcTheme.typography.titleLarge,
                    color = colors.foreground
                )
                Text(
                    text = "${activeWeekNumber}. hafta · ${currentPlan?.metadata?.academicYear ?: "2026-2027"}",
                    style = ArcTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
            }
            if (onToggleSidebar != null) {
                IconButton(onClick = onToggleSidebar, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = if (sidebarVisible) "Sol paneli gizle" else "Sol paneli aç",
                        tint = colors.textSecondary
                    )
                }
            }
            IconButton(onClick = { showSearch = !showSearch }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Search, contentDescription = "Plan araması", tint = colors.textSecondary)
            }
            IconButton(onClick = { viewModel.setCommandPaletteOpen(true) }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Keyboard, contentDescription = "Komut paleti", tint = colors.textSecondary)
            }
            IconButton(onClick = { viewModel.setNotificationCenterOpen(true) }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = colors.textSecondary)
            }
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(40.dp).testTag("settings-open-button")
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Ayarlar", tint = colors.textSecondary)
            }
        }

        if (storageError != null) {
            Text(
                text = storageError.orEmpty(),
                style = ArcTheme.typography.bodyMedium,
                color = colors.danger
            )
        }

        if (showSearch || filter.query.isNotBlank()) {
            PlanSearchPanel(
                query = filter.query,
                results = searchResults,
                onQueryChange = viewModel::setFilterQuery,
                onSelectWeek = {
                    viewModel.openSearchResult(it)
                    showSearch = false
                }
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Takvime göre ilerleme", style = ArcTheme.typography.labelSmall, color = colors.textSecondary)
                    Text(
                        "${timeline.percent}% · ${timeline.elapsedHours} / ${timeline.totalHours} saat",
                        style = ArcTheme.typography.labelSmall, color = colors.textSecondary
                    )
                }
                ArcProgress(progress = completion.coerceIn(0f, 1f), height = 6.dp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            ArcButton(onClick = { showStats = !showStats }, variant = ArcButtonVariant.SUBTLE) {
                Text(if (showStats) "Özeti gizle" else "Ayrıntılar")
            }
        }
        if (showStats) {
            Text(
                "İşlendi olarak işaretlenen: $completedHours / ${timeline.totalHours} saat. Bu değer takvim ilerlemesinden ayrıdır.",
                style = ArcTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            ArcStatsBand(
                totalWeeks = currentPlan?.metadata?.totalTeachingWeeks ?: 37,
                totalLessons = currentPlan?.weeks?.sumOf { it.lessons.size } ?: 0,
                verifiedHours = currentPlan?.metadata?.verifiedLessonHours,
                assumedHours = currentPlan?.metadata?.assumedLessonHours,
                annualHours = currentPlan?.metadata?.totalAnnualHoursAssumed
                    ?: currentPlan?.metadata?.totalAnnualHours
                    ?: timeline.totalHours,
                completedHours = completedHours,
                currentWeekNumber = activeWeekNumber
            )
        }

        ArcButton(
            onClick = { showViews = !showViews },
            variant = ArcButtonVariant.SUBTLE,
            modifier = Modifier.testTag("planner-view-toggle")
        ) {
            Text(if (showViews) "Görünümleri gizle" else "Görünümler: ${viewMode.title}")
        }
        if (showViews) {
            ArcSegmentedControl(
                options = PlannerViewMode.visibleModes,
                selected = viewMode,
                onSelect = {
                    viewModel.selectViewMode(it)
                    showViews = false
                },
                title = { it.title },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Main View Router
        when (viewMode) {
            PlannerViewMode.WEEK -> {
                WeeklyLessonPlanView(
                    viewModel = viewModel,
                    activeWeek = activeWeek,
                    activeWeekNumber = activeWeekNumber,
                    showWeekSummary = onToggleSidebar == null || !sidebarVisible
                )
            }
            PlannerViewMode.MONTH -> {
                MonthlyOverviewView(viewModel = viewModel)
            }
            PlannerViewMode.THEME -> {
                ThematicCurriculumView(currentPlan = currentPlan)
            }
            PlannerViewMode.ANALYTICS -> {
                AnalyticsAndChartsView(viewModel = viewModel, currentPlan = currentPlan)
            }
            // Legacy serialized view states are migrated to WEEK; never show removed screens.
            else -> WeeklyLessonPlanView(
                viewModel = viewModel,
                activeWeek = activeWeek,
                activeWeekNumber = activeWeekNumber,
                showWeekSummary = onToggleSidebar == null || !sidebarVisible
            )
        }
        // Leave a comfortable scroll destination above Android's bottom system bar.
        Spacer(modifier = Modifier.height(ArcSpacing.space4))
    }
}

