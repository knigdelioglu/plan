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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.blocks.ArcChangelogFeed
import com.plan.app.core.design.blocks.ArcCommandPalette
import com.plan.app.core.design.blocks.ArcComparisonTable
import com.plan.app.core.design.blocks.ArcEmptyStatesBlock
import com.plan.app.core.design.blocks.ArcFaqSection
import com.plan.app.core.design.blocks.ArcFileUpload
import com.plan.app.core.design.blocks.ArcNotificationCenter
import com.plan.app.core.design.blocks.ArcPageHeader
import com.plan.app.core.design.blocks.ArcStatsBand
import com.plan.app.core.design.blocks.CommandItem
import com.plan.app.core.design.components.advanced.ArcActivityHeatmap
import com.plan.app.core.design.components.advanced.ArcCarousel
import com.plan.app.core.design.components.advanced.ArcConfirmMorph
import com.plan.app.core.design.components.advanced.ArcCopyButton
import com.plan.app.core.design.components.advanced.ArcDatePicker
import com.plan.app.core.design.components.advanced.ArcExpandingSearch
import com.plan.app.core.design.components.advanced.ArcFloatingButtonGroup
import com.plan.app.core.design.components.advanced.ArcHoldToConfirm
import com.plan.app.core.design.components.advanced.ArcInlineEdit
import com.plan.app.core.design.components.advanced.ArcInput
import com.plan.app.core.design.components.advanced.ArcMetricCard
import com.plan.app.core.design.components.advanced.ArcMorphSelect
import com.plan.app.core.design.components.advanced.ArcMultiSelect
import com.plan.app.core.design.components.advanced.ArcRadioGroup
import com.plan.app.core.design.components.advanced.ArcSplitButton
import com.plan.app.core.design.components.advanced.ArcStepper
import com.plan.app.core.design.components.advanced.ArcSwipeActions
import com.plan.app.core.design.components.advanced.ArcSwitch
import com.plan.app.core.design.components.advanced.ArcTextarea
import com.plan.app.core.design.components.advanced.ArcThemeSwitch
import com.plan.app.core.design.components.advanced.ArcTimeline
import com.plan.app.core.design.components.advanced.ArcToastStack
import com.plan.app.core.design.components.advanced.ArcTreeView
import com.plan.app.core.design.components.advanced.QueuedToast
import com.plan.app.core.design.components.advanced.TimelineMilestone
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
import com.plan.app.core.design.components.core.ArcChipGroup
import com.plan.app.core.design.components.core.ArcDialog
import com.plan.app.core.design.components.core.ArcDrawer
import com.plan.app.core.design.components.core.ArcExpandableCard
import com.plan.app.core.design.components.core.ArcFilterToolbar
import com.plan.app.core.design.components.core.ArcResizablePanels
import com.plan.app.core.design.components.core.ArcScrollArea
import com.plan.app.core.design.components.core.ArcSegmentedControl
import com.plan.app.core.design.components.core.ArcSelect
import com.plan.app.core.design.components.core.ArcTabs
import com.plan.app.core.design.components.core.ArcTooltip
import com.plan.app.core.design.components.core.RadioCardOption
import com.plan.app.core.design.components.extended.ArcAnimatedCounter
import com.plan.app.core.design.components.extended.ArcBarChart
import com.plan.app.core.design.components.extended.ArcCardStack
import com.plan.app.core.design.components.extended.ArcDonutChart
import com.plan.app.core.design.components.extended.ArcExpandingButtonGroup
import com.plan.app.core.design.components.extended.ArcJsonViewer
import com.plan.app.core.design.components.extended.ArcLineChart
import com.plan.app.core.design.components.extended.ArcPagination
import com.plan.app.core.design.components.extended.ArcRichTextEditor
import com.plan.app.core.design.components.extended.ArcSortableDataTable
import com.plan.app.core.design.components.extended.ArcTextMorph
import com.plan.app.core.design.components.extended.ArcTextReveal
import com.plan.app.core.design.components.extended.ArcUsageMeter
import com.plan.app.core.design.components.extended.ArcUserMenu
import com.plan.app.core.design.components.extended.BarChartItem
import com.plan.app.core.design.components.extended.DonutSlice
import com.plan.app.core.design.components.extended.ExpandingAction
import com.plan.app.core.design.components.extended.TableColumn
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
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
    val changelogEntries by viewModel.changelogEntries.collectAsState()
    val filter by viewModel.filter.collectAsState()

    val currentClass = classrooms.firstOrNull { it.id == activeClassroomId } ?: classrooms.firstOrNull()
    val activeWeek = viewModel.getActiveWeek()
    val colors = ArcTheme.colors

    var showNewClassDialog by remember { mutableStateOf(false) }
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
        // An actual sidebar calendar needs at least ~350dp for usable touch targets.
        val isTabletLandscape = maxWidth >= 950.dp

        if (isTabletLandscape) {
            // Tablet-first Resizable Paneller
            ArcResizablePanels(
                initialSplitRatio = 0.30f,
                minFirstPanelWidth = 390.dp,
                minSecondPanelWidth = 480.dp,
                firstPanel = {
                    LeftSidebar(
                        viewModel = viewModel,
                        currentClass = currentClass,
                        classrooms = classrooms,
                        onOpenNewClassDialog = { showNewClassDialog = true }
                    )
                },
                secondPanel = {
                    CenterWorkspace(
                        viewModel = viewModel,
                        activeWeek = activeWeek,
                        currentClass = currentClass,
                        viewMode = viewMode
                    )
                }
            )
        } else {
            // Compact / Phone layout
            Column(modifier = Modifier.fillMaxSize()) {
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
    onOpenNewClassDialog: () -> Unit
) {
    val colors = ArcTheme.colors
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()

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

                IconButton(onClick = onOpenNewClassDialog, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Sınıf Ekle", tint = colors.accent)
                }
            }

            // Quick Search
            ArcExpandingSearch(
                query = viewModel.filter.collectAsState().value.query,
                onQueryChange = { viewModel.setFilterQuery(it) }
            )

            // Mini Calendar
            ArcCalendar(
                selectedWeek = activeWeekNumber,
                onSelectWeek = {
                    viewModel.selectWeek(it)
                    viewModel.selectViewMode(PlannerViewMode.WEEK)
                },
                planWeeks = currentPlan?.weeks ?: emptyList(),
                academicBreaks = currentPlan?.academicCalendarBreaks ?: emptyList()
            )

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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ArcThemeSwitch(
                    isDark = viewModel.isDarkMode.collectAsState().value,
                    onToggle = { viewModel.toggleDarkMode() }
                )
            }
        }
    }
}

@Composable
private fun CenterWorkspace(
    viewModel: PlannerViewModel,
    activeWeek: PlanWeek?,
    currentClass: Classroom?,
    viewMode: PlannerViewMode
) {
    val colors = ArcTheme.colors
    val activeWeekNumber by viewModel.activeWeekNumber.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val activeClassroomId by viewModel.activeClassroomId.collectAsState()
    val progress by viewModel.progressList.collectAsState()
    val completedHours = currentPlan?.weeks.orEmpty().sumOf { week ->
        week.lessons.filter { lesson ->
            progress.any {
                it.classroomId == activeClassroomId &&
                    it.weekNumber == week.weekNumber &&
                    it.lessonIndex == lesson.lessonIndex && it.isCompleted
            }
        }.sumOf { it.hours }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ArcSpacing.space4)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)
    ) {
        // Top Page Header (Block 1)
        ArcPageHeader(
            title = currentClass?.name ?: "11. Sınıf TDE",
            breadcrumb = "Yıllık Plan • ${currentPlan?.metadata?.academicYear ?: "2026-2027"}",
            subtitle = currentPlan?.metadata?.documentTitle,
            badgeText = "${activeWeekNumber}. Hafta",
            badgeVariant = ArcBadgeVariant.ACCENT,
            actions = {
                ArcButton(
                    onClick = { viewModel.setCommandPaletteOpen(true) },
                    variant = ArcButtonVariant.SECONDARY
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Cmd+K")
                    }
                }
                ArcActionButton(
                    onClick = { viewModel.setNotificationCenterOpen(true) },
                    variant = ArcButtonVariant.SECONDARY
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", modifier = Modifier.size(18.dp))
                }
                ArcThemeSwitch(
                    isDark = isDarkMode,
                    onToggle = { viewModel.toggleDarkMode() }
                )
            }
        )

        // Stats Band (Block 8)
        ArcStatsBand(
            totalWeeks = currentPlan?.metadata?.totalTeachingWeeks ?: 37,
            totalLessons = currentPlan?.weeks?.sumOf { it.lessons.size } ?: 0,
            verifiedHours = currentPlan?.metadata?.verifiedLessonHours,
            assumedHours = currentPlan?.metadata?.assumedLessonHours,
            annualHours = currentPlan?.metadata?.totalAnnualHoursAssumed
                ?: currentPlan?.metadata?.totalAnnualHours
                ?: currentPlan?.weeks?.sumOf { it.totalHours } ?: 0,
            completedHours = completedHours,
            currentWeekNumber = activeWeekNumber
        )

        // View Mode Segmented Control (Component 19)
        ArcSegmentedControl(
            options = PlannerViewMode.visibleModes,
            selected = viewMode,
            onSelect = { viewModel.selectViewMode(it) },
            title = { it.title },
            modifier = Modifier.fillMaxWidth()
        )

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
        ArcEmptyStatesBlock.PlanNotAvailable(plan?.gradeLevel ?: 11, plan?.courseName ?: "Türk Dili ve Edebiyatı", {})
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(ArcSpacing.space4)) {
        // Week Header Card
        ArcCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ArcSpacing.space4),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${activeWeek.weekNumber}. Hafta (${activeWeek.month})",
                            style = ArcTheme.typography.titleLarge,
                            color = colors.foreground
                        )
                        ArcBadge(
                            text = activeWeek.dateRangeRaw,
                            variant = ArcBadgeVariant.NEUTRAL
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${activeWeek.theme?.themeCode}: ${activeWeek.theme?.themeName}",
                        style = ArcTheme.typography.bodyMedium,
                        color = colors.accent
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ArcButton(
                        onClick = { viewModel.selectWeek(activeWeekNumber - 1) },
                        enabled = activeWeekNumber > 1,
                        variant = ArcButtonVariant.SECONDARY
                    ) {
                        Text("← Önceki Hafta")
                    }
                    ArcButton(
                        onClick = { viewModel.selectWeek(activeWeekNumber + 1) },
                        enabled = activeWeekNumber < 37,
                        variant = ArcButtonVariant.SECONDARY
                    ) {
                        Text("Sonraki Hafta →")
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
                onSwipeLeft = { viewModel.toggleLessonProgress(activeWeek.weekNumber, lesson.lessonIndex) },
                onSwipeRight = { viewModel.toggleLessonProgress(activeWeek.weekNumber, lesson.lessonIndex) }
            ) {
                ArcCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isCompleted) colors.success.copy(alpha = 0.5f) else colors.border
                ) {
                    Column(modifier = Modifier.padding(ArcSpacing.space4)) {
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
                                    onCheckedChange = {
                                        viewModel.toggleLessonProgress(activeWeek.weekNumber, lesson.lessonIndex)
                                    }
                                )
                                Column {
                                    Text(
                                        text = "${lesson.lessonIndex}. Bölüm: ${lesson.learningArea ?: "Ders Alanı"}",
                                        style = ArcTheme.typography.titleMedium,
                                        color = if (isCompleted) colors.textSecondary else colors.foreground
                                    )
                                    Text(
                                        text = "${lesson.hours} Ders Saati (${lesson.hoursSourceStatus ?: "doğrulandı"})",
                                        style = ArcTheme.typography.labelSmall,
                                        color = colors.textMuted
                                    )
                                }
                            }

                            if (isCompleted) {
                                ArcBadge(text = "Tamamlandı", variant = ArcBadgeVariant.SUCCESS)
                            }
                        }

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

                        // Learning Outcomes
                        if (lesson.learningOutcomes.isNotEmpty()) {
                            Text(
                                text = "Öğrenme Çıktıları (Kazanımlar):",
                                style = ArcTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            lesson.learningOutcomes.forEach { outcome ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ArcBadge(text = outcome.code, variant = ArcBadgeVariant.ACCENT)
                                    Text(
                                        text = outcome.text,
                                        style = ArcTheme.typography.bodySmall,
                                        color = colors.foreground
                                    )
                                }
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
                    }
                }
            }
        }

        // Pedagogical Context & Teacher Notes Side-by-Side or Stacked
        TeacherNotesAndPedagogicalSection(viewModel = viewModel, activeWeek = activeWeek)
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
        mutableStateOf(existingNote?.text ?: "")
    }

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
        ArcRichTextEditor(
            value = noteText,
            onValueChange = {
                noteText = it
                viewModel.saveTeacherNote(it, NoteType.TEXT)
            }
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
                        text = theme.assessmentAndEvaluation?.text?.take(200) + "...",
                        style = ArcTheme.typography.bodySmall,
                        color = colors.foreground
                    )
                }
            }
        }
    }
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
