package com.plan.app.feature.planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.plan.app.core.data.PlanRepository
import com.plan.app.core.data.TeacherRepository
import com.plan.app.core.design.blocks.ComparisonRow
import com.plan.app.core.design.components.extended.JsonNode
import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.ChangelogEntry
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

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _filter = MutableStateFlow(PlanFilter())
    val filter: StateFlow<PlanFilter> = _filter.asStateFlow()

    private val _isCommandPaletteOpen = MutableStateFlow(false)
    val isCommandPaletteOpen: StateFlow<Boolean> = _isCommandPaletteOpen.asStateFlow()

    private val _isNotificationCenterOpen = MutableStateFlow(false)
    val isNotificationCenterOpen: StateFlow<Boolean> = _isNotificationCenterOpen.asStateFlow()

    private val _currentPlan = MutableStateFlow<AnnualPlan?>(null)
    val currentPlan: StateFlow<AnnualPlan?> = _currentPlan.asStateFlow()

    private val _fenPlan = MutableStateFlow<AnnualPlan?>(null)
    val fenPlan: StateFlow<AnnualPlan?> = _fenPlan.asStateFlow()

    private val _rawPlanJson = MutableStateFlow("")
    val rawPlanJson: StateFlow<String> = _rawPlanJson.asStateFlow()

    private val _validationReport = MutableStateFlow("")
    val validationReport: StateFlow<String> = _validationReport.asStateFlow()

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
            ),
            NotificationItem(
                id = "notif-3",
                title = "Kaynak Plan Doğrulandı",
                message = "180 ders saati özgün 11. Sınıf Excel arşiviyle birebir doğrulanmıştır.",
                timestamp = "Ekim 2026",
                type = NotificationType.VALIDATION_NOTICE
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _changelogEntries = MutableStateFlow(
        listOf(
            com.plan.app.core.model.ChangelogEntry(
                version = "v2.0.0",
                date = "10 Ekim 2026",
                title = "11. Sınıf TDE Anadolu ve Fen Lisesi Doğrulaması",
                details = listOf(
                    "37 haftanın tarihleri, temalar, kazanım ve pedagojik açıklamaları kayıpsız aktarıldı.",
                    "3+2 saatlik çoklu ders bölümleri hiyerarşik olarak korundu.",
                    "15 Temmuz anmasının açılış haftasına yerleşimi doğrulandı."
                ),
                author = "Yıllık Plan Arşivi"
            )
        )
    )
    val changelogEntries: StateFlow<List<com.plan.app.core.model.ChangelogEntry>> = _changelogEntries.asStateFlow()

    init {
        loadActivePlan()
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
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
        teacherRepository.selectWeek(weekNumber.coerceIn(1, 37))
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

    fun addClassroom(name: String, schoolType: String): Classroom {
        val newClass = teacherRepository.addClassroom(name, schoolType, 11)
        selectClassroom(newClass.id)
        return newClass
    }

    fun getComparisonRows(): List<ComparisonRow> {
        val a = _currentPlan.value
        val f = _fenPlan.value
        return listOf(
            ComparisonRow(
                feature = "Toplam Hafta Sayısı",
                anadoluValue = "${a?.metadata?.totalTeachingWeeks ?: 37} Hafta",
                fenValue = "${f?.metadata?.totalTeachingWeeks ?: 37} Hafta"
            ),
            ComparisonRow(
                feature = "Haftalık Ders Saati",
                anadoluValue = "${a?.metadata?.weeklyLessonHours ?: 5} Saat",
                fenValue = "${f?.metadata?.weeklyLessonHours ?: 5} Saat"
            ),
            ComparisonRow(
                feature = "Doğrulanmış Saatler",
                anadoluValue = "${a?.metadata?.totalAnnualHoursVerified ?: 180} Saat",
                fenValue = "${f?.metadata?.totalAnnualHoursVerified ?: 180} Saat"
            ),
            ComparisonRow(
                feature = "3+2 Saat Dağılımı",
                anadoluValue = "8 Hafta (16 Ders Bölümü)",
                fenValue = "8 Hafta (16 Ders Bölümü)"
            ),
            ComparisonRow(
                feature = "Özgün Belge Başlığı",
                anadoluValue = a?.metadata?.documentTitle ?: "",
                fenValue = f?.metadata?.documentTitle ?: "",
                isDifferent = true
            ),
            ComparisonRow(
                feature = "1. Hafta Özel Gün",
                anadoluValue = "15 Temmuz Demokrasi Günü",
                fenValue = "15 Temmuz Demokrasi Günü"
            )
        )
    }

    fun getJsonViewerRootNode(): JsonNode {
        val plan = _currentPlan.value ?: return JsonNode("plan", "Yükleniyor...")
        return JsonNode(
            key = "annual_plan",
            children = listOf(
                JsonNode(
                    key = "metadata",
                    children = listOf(
                        JsonNode("academic_year", plan.metadata.academicYear),
                        JsonNode("grade_level", plan.metadata.gradeLevel.toString()),
                        JsonNode("course_name", plan.metadata.courseName),
                        JsonNode("school_type", plan.metadata.schoolType),
                        JsonNode("verified_lesson_hours", plan.metadata.verifiedLessonHours.toString()),
                        JsonNode("assumed_lesson_hours", plan.metadata.assumedLessonHours.toString())
                    )
                ),
                JsonNode(
                    key = "general_explanations",
                    children = listOf(
                        JsonNode("differentiation", plan.generalExplanations?.differentiation?.title),
                        JsonNode("school_based_planning", plan.generalExplanations?.schoolBasedPlanning?.title)
                    )
                ),
                JsonNode(
                    key = "academic_calendar_breaks",
                    children = plan.academicCalendarBreaks.map {
                        JsonNode(it.breakName, "${it.startDate} — ${it.endDate}")
                    }
                ),
                JsonNode(
                    key = "themes",
                    children = plan.themes.map {
                        JsonNode("${it.themeCode}: ${it.themeName}", "${it.totalHours} saat")
                    }
                ),
                JsonNode(
                    key = "weeks",
                    children = plan.weeks.take(5).map { w ->
                        JsonNode(
                            "${w.weekNumber}. Hafta (${w.dateRangeRaw})",
                            "${w.totalHours} saat, ${w.lessons.size} ders bölümü"
                        )
                    }
                )
            )
        )
    }

    private fun loadActivePlan() {
        val currentClass = classrooms.value.firstOrNull { it.id == activeClassroomId.value }
            ?: classrooms.value.firstOrNull()
        val schoolType = currentClass?.schoolType ?: "Anadolu Lisesi"

        try {
            _currentPlan.value = planRepository.loadAnnualPlan(schoolType)
            _fenPlan.value = planRepository.loadAnnualPlan("Fen Lisesi")
            _rawPlanJson.value = planRepository.getRawPlanJson(schoolType)
            _validationReport.value = planRepository.loadValidationReport(schoolType)
        } catch (e: Exception) {
            // handle error
        }
    }
}
