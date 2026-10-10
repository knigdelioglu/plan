package com.plan.app.feature.planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plan.app.core.data.PlanRepository
import com.plan.app.core.data.TeacherRepository
import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.DrawingStroke
import com.plan.app.core.model.LessonProgress
import com.plan.app.core.model.NoteType
import com.plan.app.core.model.NotificationItem
import com.plan.app.core.model.NotificationType
import com.plan.app.core.model.PlanFilter
import com.plan.app.core.model.PlanWeek
import com.plan.app.core.model.PlannerViewMode
import com.plan.app.core.model.TeacherNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlannerViewModel @JvmOverloads constructor(
    application: Application,
    private val planRepository: PlanRepository = PlanRepository(application),
    private val teacherRepository: TeacherRepository = TeacherRepository(application)
) : AndroidViewModel(application) {

    val classrooms: StateFlow<List<Classroom>> = teacherRepository.classrooms
    val activeClassroomId: StateFlow<String> = teacherRepository.activeClassroomId
    val activeWeekNumber: StateFlow<Int> = teacherRepository.activeWeekNumber
    val viewMode: StateFlow<PlannerViewMode> = teacherRepository.viewMode
    val progressList: StateFlow<List<LessonProgress>> = teacherRepository.progressList
    val notesList: StateFlow<List<TeacherNote>> = teacherRepository.notesList

    val isDarkMode: StateFlow<Boolean> = teacherRepository.darkMode
    val storageError: StateFlow<String?> = teacherRepository.storageError
    val availablePlans: List<PlanRepository.PlanRef>
        get() = planRepository.availablePlans()

    private val _filter = MutableStateFlow(PlanFilter())
    val filter: StateFlow<PlanFilter> = _filter.asStateFlow()

    private val _isCommandPaletteOpen = MutableStateFlow(false)
    val isCommandPaletteOpen: StateFlow<Boolean> = _isCommandPaletteOpen.asStateFlow()

    private val _isNotificationCenterOpen = MutableStateFlow(false)
    val isNotificationCenterOpen: StateFlow<Boolean> = _isNotificationCenterOpen.asStateFlow()

    private val _currentPlan = MutableStateFlow<AnnualPlan?>(null)
    val currentPlan: StateFlow<AnnualPlan?> = _currentPlan.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "notif-1",
                title = "15 Temmuz Açılış Anması",
                message = "15 Temmuz Demokrasi ve Millî Birlik Günü anma programı 1. haftaya planlanmıştır.",
                timestamp = "14 Eylül 2026",
                type = NotificationType.SPECIAL_DAY
            ),
            NotificationItem(
                id = "notif-2",
                title = "1. Dönem Ara Tatili",
                message = "16 - 20 Kasım 2026 tarihleri arasında 1. dönem ara tatili uygulanacaktır.",
                timestamp = "16 Kasım 2026",
                type = NotificationType.ACADEMIC_BREAK

            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        loadActivePlan()
    }

    fun toggleDarkMode() {
        teacherRepository.setDarkMode(!isDarkMode.value)
    }

    fun setCommandPaletteOpen(open: Boolean) {
        _isCommandPaletteOpen.value = open
    }

    fun setNotificationCenterOpen(open: Boolean) {
        _isNotificationCenterOpen.value = open
    }

    fun selectClassroom(id: String) {
        teacherRepository.selectClassroom(id)
        loadActivePlan()
    }

    fun selectWeek(weekNumber: Int) {
        val maxWeek = _currentPlan.value?.weeks?.maxOfOrNull { it.weekNumber } ?: 1
        teacherRepository.selectWeek(weekNumber.coerceIn(1, maxWeek))
    }

    fun selectViewMode(mode: PlannerViewMode) {
        teacherRepository.selectViewMode(mode)
    }

    fun setFilterQuery(query: String) {
        _filter.value = _filter.value.copy(query = query)
    }

    fun setFilterTheme(themeNumber: Int?) {
        _filter.value = _filter.value.copy(selectedThemeNumber = themeNumber)
    }

    fun toggleFilterOnlySpecialDays() {
        _filter.value = _filter.value.copy(onlySpecialDays = !_filter.value.onlySpecialDays)
    }

    fun toggleFilterOnlyIncomplete() {
        _filter.value = _filter.value.copy(onlyIncomplete = !_filter.value.onlyIncomplete)
    }

    fun resetFilter() {
        _filter.value = PlanFilter()
    }

    fun toggleLessonProgress(weekNumber: Int, lessonIndex: Int): Boolean {
        val cid = activeClassroomId.value
        return teacherRepository.toggleLessonProgress(cid, weekNumber, lessonIndex)
    }

    fun isLessonCompleted(weekNumber: Int, lessonIndex: Int): Boolean {
        val cid = activeClassroomId.value
        return teacherRepository.isLessonCompleted(cid, weekNumber, lessonIndex)
    }

    fun getActiveWeek(): PlanWeek? {
        val plan = _currentPlan.value ?: return null
        return plan.weeks.firstOrNull { it.weekNumber == activeWeekNumber.value }
    }

    fun getActiveTeacherNote(): TeacherNote? {
        val cid = activeClassroomId.value
        val week = activeWeekNumber.value
        return teacherRepository.getNote(cid, week, null)
    }

    fun saveTeacherNote(text: String, type: NoteType = NoteType.TEXT, drawingStrokes: List<DrawingStroke> = emptyList()) {
        val cid = activeClassroomId.value
        val week = activeWeekNumber.value
        teacherRepository.saveNote(cid, week, null, text, type, drawingStrokes)
    }

    fun getCompletedHours(): Int {
        val plan = _currentPlan.value ?: return 0
        return teacherRepository.getCompletedHours(activeClassroomId.value, plan.weeks)
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun addClassroom(name: String, planRef: PlanRepository.PlanRef): Classroom {
        val newClass = teacherRepository.addClassroom(
            name, planRef.schoolType, planRef.gradeLevel, planRef.academicYear, planRef.courseName
        )
        selectClassroom(newClass.id)
        return newClass
    }

    private fun loadActivePlan() {
        val currentClass = classrooms.value.firstOrNull { it.id == activeClassroomId.value }
            ?: classrooms.value.firstOrNull()
        // Only the active classroom's plan is loaded: no hidden comparisons,
        // raw JSON inspection, or validation reports on every classroom change.
        _currentPlan.value = runCatching {
            currentClass?.let(planRepository::loadAnnualPlan)
        }.getOrNull()
    }
}
