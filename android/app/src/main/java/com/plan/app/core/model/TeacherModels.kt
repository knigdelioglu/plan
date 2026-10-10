package com.plan.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Classroom(
    val id: String,
    val name: String,
    val gradeLevel: Int,
    val schoolType: String,
    val academicYear: String,
    val courseName: String,
    val isDefault: Boolean = false
)

@Serializable
data class LessonProgress(
    val classroomId: String,
    val weekNumber: Int,
    val lessonIndex: Int,
    val isCompleted: Boolean,
    val completedAt: Long? = null
)

@Serializable
data class TeacherNote(
    val id: String,
    val classroomId: String,
    val weekNumber: Int,
    val lessonIndex: Int? = null,
    val text: String = "",
    val noteType: NoteType = NoteType.TEXT,
    val drawingPoints: List<DrawingStroke> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class NoteType {
    TEXT,
    DRAWING,
    HYBRID
}

@Serializable
data class DrawingStroke(
    val points: List<DrawingPoint>,
    val colorHex: String = "#18181B",
    val strokeWidth: Float = 4f
)

@Serializable
data class DrawingPoint(
    val x: Float,
    val y: Float
)

enum class PlannerViewMode(val title: String) {
    WEEK("Haftalık"),
    MONTH("Aylık"),
    THEME("Tematik"),
    YEAR("Yıllık"),
    COMPARISON("Karşılaştırma"),
    ANALYTICS("Analiz"),
    VALIDATION("Doğrulama & JSON"),
    FAQ("Rehber & SSS")
}

data class PlanFilter(
    val query: String = "",
    val selectedThemeNumber: Int? = null,
    val selectedMonth: String? = null,
    val onlySpecialDays: Boolean = false,
    val onlyIncomplete: Boolean = false
)

data class ChangelogEntry(
    val version: String,
    val date: String,
    val title: String,
    val details: List<String>,
    val author: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    INFO,
    ACADEMIC_BREAK,
    SPECIAL_DAY,
    VALIDATION_NOTICE,
    SYSTEM
}
