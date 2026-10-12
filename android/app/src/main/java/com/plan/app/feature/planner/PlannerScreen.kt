package com.plan.app.feature.planner

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
import com.plan.app.core.design.components.advanced.ArcThemeSwitch
import com.plan.app.core.design.components.advanced.ArcTreeView
import com.plan.app.core.design.components.advanced.TreeNode
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
import java.time.LocalDate
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.NoteType
import com.plan.app.core.model.PlanLesson
import com.plan.app.core.model.PlanWeek
import com.plan.app.core.model.PlannerViewMode

@Composable
fun PlannerScreen(viewModel: PlannerViewModel) {
    val classrooms by viewModel.classrooms.collectAsState()
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
    val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val filter by viewModel.filter.collectAsState()

    val currentClass = classrooms.firstOrNull { it.id == activeClassroomId } ?: classrooms.firstOrNull()
    // Derive from collected Compose state so the lesson content follows week navigation.
    val activeWeek = currentPlan?.weeks?.firstOrNull { it.weekNumber == activeWeekNumber }
    val colors = ArcTheme.colors

    var showNewClassDialog by rememberSaveable { mutableStateOf(false) }
    // Hoisted above the compact/two-pane branch: DeX resize does not reset it.
    var sidebarRatio by rememberSaveable { mutableFloatStateOf(0.29f) }
    var sidebarCollapsed by rememberSaveable { mutableStateOf(false) }
    var newClassName by remember { mutableStateOf("") }
    val availablePlans = viewModel.availablePlans
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
        modifier = Modifier.fillMaxSize().background(colors.background)
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
                    CenterWorkspace(
                        viewModel = viewModel,
                        activeWeek = activeWeek,
                        currentClass = currentClass,
                        viewMode = viewMode,
                        onToggleSidebar = { sidebarCollapsed = !sidebarCollapsed },
                        sidebarVisible = true
                    )
                }
            )
        } else if (isTabletLandscape) {
            Box(modifier = Modifier.fillMaxSize().testTag("planner-two-pane")) {
                CenterWorkspace(
                    viewModel = viewModel,
                    activeWeek = activeWeek,
                    currentClass = currentClass,
                    viewMode = viewMode,
                    onToggleSidebar = { sidebarCollapsed = false },
                    sidebarVisible = false
                )
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
                    CenterWorkspace(
                        viewModel = viewModel,
                        activeWeek = activeWeek,
                        currentClass = currentClass,
                        viewMode = viewMode
                    )
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

            // Tree View (Themes & Weeks)
            val themes = currentPlan?.themes ?: emptyList()
            if (themes.isNotEmpty()) {
                Text(
                    text = "Müfredat Hiyerarşisi",
                    style = ArcTheme.typography.labelSmall,
                    color = colors.textMuted
                )
                val treeNodes = themes.map { theme ->
                    TreeNode(
                        id = "theme-${theme.themeNumber}",
                        label = "${theme.themeCode}: ${theme.themeName}",
                        badgeText = "${theme.totalHours} sa",
                        children = run {
                            val startW = theme.weekRange.firstOrNull() ?: 1
                            val endW = theme.weekRange.lastOrNull() ?: 9
                            (startW..endW).map { wNum ->
                                TreeNode(
                                    id = "week-$wNum",
                                    label = "$wNum. Hafta",
                                    onClick = {
                                        viewModel.selectWeek(wNum)
                                        viewModel.selectViewMode(PlannerViewMode.WEEK)
                                    }
                                )
                            }
                        }
                    )
                }
                ArcTreeView(nodes = treeNodes)
            }
        }
    }
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
private fun CenterWorkspace(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek?,
    currentClass: Classroom?,
    viewMode: PlannerViewMode,
    onToggleSidebar: (() -> Unit)? = null,
    sidebarVisible: Boolean = true
) {
    val colors = ArcTheme.colors
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val searchResults = viewModel.searchResults()
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
    val annualHours = currentPlan?.weeks?.sumOf { it.totalHours } ?: 0
    val completion = if (annualHours > 0) completedHours.toFloat() / annualHours else 0f

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
            ArcThemeSwitch(isDark = isDarkMode, onToggle = { viewModel.toggleDarkMode() })
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
                    Text("Yıllık ilerleme", style = ArcTheme.typography.labelSmall, color = colors.textSecondary)
                    Text(
                        "${(completion * 100).toInt()}% · $completedHours / $annualHours saat",
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
            ArcStatsBand(
                totalWeeks = currentPlan?.metadata?.totalTeachingWeeks ?: 37,
                totalLessons = currentPlan?.weeks?.sumOf { it.lessons.size } ?: 0,
                verifiedHours = currentPlan?.metadata?.verifiedLessonHours,
                assumedHours = currentPlan?.metadata?.assumedLessonHours,
                annualHours = currentPlan?.metadata?.totalAnnualHoursAssumed
                    ?: currentPlan?.metadata?.totalAnnualHours
                    ?: annualHours,
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
                    activeWeekNumber = activeWeekNumber
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
                activeWeekNumber = activeWeekNumber
            )
        }
    }
}

@Composable
private fun WeeklyLessonPlanView(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek?,
    activeWeekNumber: Int
) {
    val colors = ArcTheme.colors
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()

    if (activeWeek == null) {
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
        // Week heading reflows instead of clipping the two navigation buttons on phones.
        ArcCard(modifier = Modifier.fillMaxWidth()) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().padding(ArcSpacing.space3)
            ) {
                val compactHeader = maxWidth < 650.dp
                if (compactHeader) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        WeekHeaderLabel(activeWeek, colors.foreground, colors.accent)
                        WeekNavigationButtons(viewModel, activeWeekNumber, totalWeeks, compact = true)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            WeekHeaderLabel(activeWeek, colors.foreground, colors.accent)
                        }
                        WeekNavigationButtons(viewModel, activeWeekNumber, totalWeeks, compact = false)
                    }
                }
            }
        }

        // Special Days Notice (e.g. 15 Temmuz Demokrasi Günü in Week 1)
        if (activeWeek.specialDaysAndWeeks?.present == true) {
            val specialDay = activeWeek.specialDaysAndWeeks
            ArcAlert(
                title = "Özel Gün ve Hafta: ${specialDay.raw ?: specialDay.text ?: specialDay.events.joinToString(", ") { it.name }}",
                message = specialDay.schedulingNote
                    ?: specialDay.events.firstOrNull()?.calendarNote
                    ?: "Kaynak yıllık planda bu hafta için belirtilmiştir.",
                variant = ArcAlertVariant.INFO
            )
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

@Composable
private fun TeacherNotesAndPedagogicalSection(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek
) {
    val colors = ArcTheme.colors
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val notes by viewModel.notesList.collectAsState()
    val existingNote = notes.firstOrNull {
        it.classroomId == activeClassroomId && it.weekNumber == activeWeek.weekNumber && it.lessonIndex == null
    }
    // Never reuse a note editor state between two classrooms at the same week.
    var noteText by remember(activeClassroomId, activeWeek.weekNumber) {
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
        key(activeClassroomId, activeWeek.weekNumber) {
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

@Composable
private fun MonthlyOverviewView(viewModel: PlannerViewModel) {
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
private fun ThematicCurriculumView(currentPlan: com.plan.app.core.model.AnnualPlan?) {
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

private fun localizedHoursSourceStatus(status: String?): String? = when (status) {
    "verified_from_cell" -> "Kaynak hücresinden doğrulandı"
    "verified_from_cell_distribution" -> "Kaynak hücre dağılımından doğrulandı"
    "assumed_from_curriculum_standard" -> "Ders çizelgesi standardına göre varsayıldı"
    else -> null
}

@Composable
private fun AnalyticsAndChartsView(
    viewModel: PlannerViewModel,
    currentPlan: com.plan.app.core.model.AnnualPlan?
) {
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()
    val completedHours = currentPlan?.weeks.orEmpty().sumOf { w ->
        w.lessons.filter { l ->
            progress.any {
                it.classroomId == activeClassroomId && it.weekNumber == w.weekNumber &&
                    it.lessonIndex == l.lessonIndex && it.isCompleted
            }
        }.sumOf { it.hours }
    }
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
