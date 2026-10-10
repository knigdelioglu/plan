package com.plan.app.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AnnualPlan(
    @SerialName("\$schema")
    val schema: String? = null,
    val metadata: PlanMetadata,
    @SerialName("general_explanations")
    val generalExplanations: GeneralExplanations? = null,
    @SerialName("academic_calendar_breaks")
    val academicCalendarBreaks: List<AcademicCalendarBreak> = emptyList(),
    val themes: List<PlanTheme> = emptyList(),
    val weeks: List<PlanWeek> = emptyList()
)

@Serializable
data class PlanMetadata(
    @SerialName("academic_year")
    val academicYear: String,
    @SerialName("grade_level")
    val gradeLevel: Int,
    @SerialName("course_name")
    val courseName: String,
    @SerialName("school_type")
    val schoolType: String,
    @SerialName("document_title")
    val documentTitle: String,
    @SerialName("source_file")
    val sourceFile: String? = null,
    @SerialName("worksheet_name")
    val worksheetName: String? = null,
    @SerialName("total_teaching_weeks")
    val totalTeachingWeeks: Int = 37,
    @SerialName("weekly_lesson_hours")
    val weeklyLessonHours: Int = 5,
    @SerialName("verified_lesson_hours")
    val verifiedLessonHours: Int = 180,
    @SerialName("assumed_lesson_hours")
    val assumedLessonHours: Int = 5,
    @SerialName("total_annual_hours_verified")
    val totalAnnualHoursVerified: Int = 180,
    @SerialName("total_annual_hours_assumed")
    val totalAnnualHoursAssumed: Int = 185,
    @SerialName("hours_accounting")
    val hoursAccounting: HoursAccounting? = null,
    @SerialName("source_range")
    val sourceRange: String? = null,
    @SerialName("generated_at")
    val generatedAt: String? = null,
    @SerialName("extractor_version")
    val extractorVersion: String? = null,
    @SerialName("archive_path_school_type_mismatch")
    val archivePathSchoolTypeMismatch: Boolean = false,
    @SerialName("archive_path_note")
    val archivePathNote: String? = null,
    @SerialName("source_verification")
    val sourceVerification: SourceVerification? = null
)

@Serializable
data class HoursAccounting(
    @SerialName("verified_teaching_weeks_count")
    val verifiedTeachingWeeksCount: Int = 36,
    @SerialName("verified_hours_weeks_1_to_36")
    val verifiedHoursWeeks1To36: Int = 180,
    @SerialName("unverified_hours_weeks_count")
    val unverifiedHoursWeeksCount: Int = 1,
    @SerialName("week_37_hours_status")
    val week37HoursStatus: String? = null,
    @SerialName("week_37_source_note")
    val week37SourceNote: String? = null,
    @SerialName("annual_total_verified")
    val annualTotalVerified: Int = 180,
    @SerialName("annual_total_assumed")
    val annualTotalAssumed: Int = 185
)

@Serializable
data class SourceVerification(
    val status: String? = null,
    @SerialName("verified_on")
    val verifiedOn: String? = null,
    @SerialName("source_workbook_sha256")
    val sourceWorkbookSha256: String? = null,
    val summary: String? = null,
    val limitations: List<String> = emptyList()
)

@Serializable
data class GeneralExplanations(
    val differentiation: ExplanationItem? = null,
    @SerialName("school_based_planning")
    val schoolBasedPlanning: ExplanationItem? = null
)

@Serializable
data class ExplanationItem(
    val title: String = "",
    val text: String = "",
    @SerialName("source_cell_range")
    val sourceCellRange: String? = null,
    @SerialName("anchor_cell")
    val anchorCell: String? = null,
    @SerialName("is_merged")
    val isMerged: Boolean = true
)

@Serializable
data class AcademicCalendarBreak(
    @SerialName("break_name")
    val breakName: String = "",
    @SerialName("date_range_raw")
    val dateRangeRaw: String = "",
    @SerialName("start_date")
    val startDate: String = "",
    @SerialName("end_date")
    val endDate: String = "",
    @SerialName("between_weeks")
    val betweenWeeks: List<Int> = emptyList(),
    @SerialName("source_row")
    val sourceRow: Int? = null,
    @SerialName("source_cell_range")
    val sourceCellRange: String? = null
)

@Serializable
data class PlanTheme(
    @SerialName("theme_number")
    val themeNumber: Int = 1,
    @SerialName("theme_code")
    val themeCode: String = "",
    @SerialName("theme_name")
    val themeName: String = "",
    @SerialName("week_range")
    val weekRange: List<Int> = emptyList(),
    @SerialName("total_hours")
    val totalHours: Int = 0,
    @SerialName("assessment_and_evaluation")
    val assessmentAndEvaluation: AssessmentAndEvaluation? = null,
    @SerialName("interdisciplinary_components")
    val interdisciplinaryComponents: InterdisciplinaryComponents? = null
)

@Serializable
data class AssessmentAndEvaluation(
    val text: String = "",
    @SerialName("source_cell_range")
    val sourceCellRange: String? = null
)

@Serializable
data class InterdisciplinaryComponents(
    @SerialName("social_emotional_learning_skills")
    val socialEmotionalLearningSkills: ComponentGroup? = null,
    val values: ComponentGroup? = null,
    @SerialName("literacy_skills")
    val literacySkills: ComponentGroup? = null
)

@Serializable
data class ComponentGroup(
    val text: String = "",
    val codes: List<String> = emptyList(),
    @SerialName("source_cell_range")
    val sourceCellRange: String? = null
)

@Serializable
data class PlanWeek(
    @SerialName("week_number")
    val weekNumber: Int,
    val month: String,
    @SerialName("date_range_raw")
    val dateRangeRaw: String,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("end_date")
    val endDate: String,
    @SerialName("total_hours")
    val totalHours: Int,
    @SerialName("hours_raw")
    val hoursRaw: String? = null,
    @SerialName("is_hours_assumed")
    val isHoursAssumed: Boolean = false,
    @SerialName("hours_source_verified")
    val hoursSourceVerified: Int? = null,
    @SerialName("hours_assumed")
    val hoursAssumed: Int? = null,
    val theme: WeekThemeRef? = null,
    @SerialName("special_days_and_weeks")
    val specialDaysAndWeeks: SpecialDaysAndWeeks? = null,
    @SerialName("pedagogical_context")
    val pedagogicalContext: PedagogicalContext? = null,
    val lessons: List<PlanLesson> = emptyList()
)

@Serializable
data class WeekThemeRef(
    @SerialName("theme_number")
    val themeNumber: Int? = null,
    @SerialName("theme_code")
    val themeCode: String? = null,
    @SerialName("theme_name")
    val themeName: String? = null
)

@Serializable
data class SpecialDaysAndWeeks(
    val present: Boolean = false,
    val raw: String? = null,
    @SerialName("source_cell")
    val sourceCell: String? = null,
    val events: List<SpecialDayEvent> = emptyList(),
    @SerialName("has_calendar_discrepancy")
    val hasCalendarDiscrepancy: Boolean = false,
    @SerialName("calendar_discrepancy_note")
    val calendarDiscrepancyNote: String? = null,
    @SerialName("scheduling_note")
    val schedulingNote: String? = null
)

@Serializable
data class SpecialDayEvent(
    val name: String = "",
    @SerialName("date_text")
    val dateText: String? = null,
    @SerialName("is_calendar_aligned")
    val isCalendarAligned: Boolean = true,
    @SerialName("calendar_note")
    val calendarNote: String? = null,
    @SerialName("is_schedule_valid")
    val isScheduleValid: Boolean = true,
    @SerialName("commemoration_date")
    val commemorationDate: String? = null,
    @SerialName("scheduled_week")
    val scheduledWeek: Int? = null,
    @SerialName("scheduled_date")
    val scheduledDate: String? = null,
    @SerialName("scheduling_status")
    val schedulingStatus: String? = null,
    @SerialName("schedule_basis")
    val scheduleBasis: String? = null
)

@Serializable
data class PedagogicalContext(
    @SerialName("assessment_and_evaluation")
    val assessmentAndEvaluation: AssessmentAndEvaluation? = null,
    @SerialName("social_emotional_learning_skills")
    val socialEmotionalLearningSkills: ComponentGroup? = null,
    val values: ComponentGroup? = null,
    @SerialName("literacy_skills")
    val literacySkills: ComponentGroup? = null,
    val differentiation: ExplanationItem? = null,
    @SerialName("school_based_planning_framework")
    val schoolBasedPlanningFramework: ExplanationItem? = null
)

@Serializable
data class PlanLesson(
    @SerialName("lesson_index")
    val lessonIndex: Int = 1,
    val hours: Int = 5,
    @SerialName("hours_raw")
    val hoursRaw: String? = null,
    @SerialName("is_hours_assumed")
    val isHoursAssumed: Boolean = false,
    @SerialName("hours_source_status")
    val hoursSourceStatus: String? = null,
    @SerialName("learning_area")
    val learningArea: String? = null,
    @SerialName("topic_title")
    val topicTitle: String? = null,
    @SerialName("topic_raw")
    val topicRaw: String? = null,
    @SerialName("texts_and_materials")
    val textsAndMaterials: List<String> = emptyList(),
    @SerialName("learning_outcomes_raw")
    val learningOutcomesRaw: String? = null,
    @SerialName("learning_outcomes")
    val learningOutcomes: List<LearningOutcome> = emptyList(),
    @SerialName("process_components_raw")
    val processComponentsRaw: String? = null,
    @SerialName("process_components")
    val processComponents: List<ProcessComponent> = emptyList()
)

@Serializable
data class LearningOutcome(
    val code: String = "",
    val text: String = ""
)

@Serializable
data class ProcessComponent(
    val code: String = "",
    val text: String = ""
)

@Serializable
data class ReviewQueue(
    val document: String? = null,
    @SerialName("generated_at")
    val generatedAt: String? = null,
    @SerialName("total_items")
    val totalItems: Int = 0,
    val items: List<ReviewQueueItem> = emptyList()
)

@Serializable
data class ReviewQueueItem(
    val id: String? = null,
    @SerialName("item_id")
    val itemId: String? = null,
    @SerialName("week_number")
    val weekNumber: Int? = null,
    val category: String? = null,
    val description: String? = null,
    val severity: String? = null,
    val status: String? = null,
    val resolution: String? = null
)
