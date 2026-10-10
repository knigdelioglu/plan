package com.plan.app.core.data

import android.content.Context
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.DrawingPoint
import com.plan.app.core.model.DrawingStroke
import com.plan.app.core.model.LessonProgress
import com.plan.app.core.model.NoteType
import com.plan.app.core.model.PlannerViewMode
import com.plan.app.core.model.TeacherNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class TeacherRepository(private val context: Context? = null) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val defaultClassrooms = listOf(
        Classroom("cls-11a", "11-A (Anadolu)", 11, "Anadolu Lisesi", "2026-2027", "Türk Dili ve Edebiyatı", isDefault = true),
        Classroom("cls-11b", "11-B (Anadolu)", 11, "Anadolu Lisesi", "2026-2027", "Türk Dili ve Edebiyatı"),
        Classroom("cls-11fen", "11-Fen (Fen Lisesi)", 11, "Fen Lisesi", "2026-2027", "Türk Dili ve Edebiyatı")
    )

    private val _classrooms = MutableStateFlow(defaultClassrooms)
    val classrooms: StateFlow<List<Classroom>> = _classrooms.asStateFlow()

    private val _progressList = MutableStateFlow<List<LessonProgress>>(emptyList())
    val progressList: StateFlow<List<LessonProgress>> = _progressList.asStateFlow()

    private val _notesList = MutableStateFlow<List<TeacherNote>>(emptyList())
    val notesList: StateFlow<List<TeacherNote>> = _notesList.asStateFlow()

    private val _activeClassroomId = MutableStateFlow("cls-11a")
    val activeClassroomId: StateFlow<String> = _activeClassroomId.asStateFlow()

    private val _activeWeekNumber = MutableStateFlow(1)
    val activeWeekNumber: StateFlow<Int> = _activeWeekNumber.asStateFlow()

    private val _viewMode = MutableStateFlow(PlannerViewMode.WEEK)
    val viewMode: StateFlow<PlannerViewMode> = _viewMode.asStateFlow()

    init {
        loadLocalData()
    }

    fun selectClassroom(id: String) {
        _activeClassroomId.value = id
        persistData()
    }

    fun selectWeek(weekNumber: Int) {
        _activeWeekNumber.value = weekNumber
    }

    fun selectViewMode(mode: PlannerViewMode) {
        _viewMode.value = mode
    }

    fun addClassroom(name: String, schoolType: String, gradeLevel: Int = 11): Classroom {
        val newClass = Classroom(
            id = "cls-" + System.currentTimeMillis(),
            name = name,
            gradeLevel = gradeLevel,
            schoolType = schoolType,
            academicYear = "2026-2027",
            courseName = "Türk Dili ve Edebiyatı"
        )
        _classrooms.value = _classrooms.value + newClass
        persistData()
        return newClass
    }

    fun toggleLessonProgress(classroomId: String, weekNumber: Int, lessonIndex: Int): Boolean {
        val current = _progressList.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        }

        val newState: Boolean
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            newState = !existing.isCompleted
            current[existingIndex] = existing.copy(
                isCompleted = newState,
                completedAt = if (newState) System.currentTimeMillis() else null
            )
        } else {
            newState = true
            current.add(
                LessonProgress(
                    classroomId = classroomId,
                    weekNumber = weekNumber,
                    lessonIndex = lessonIndex,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
        _progressList.value = current
        persistData()
        return newState
    }

    fun isLessonCompleted(classroomId: String, weekNumber: Int, lessonIndex: Int): Boolean {
        return _progressList.value.any {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex && it.isCompleted
        }
    }

    fun getCompletedHours(classroomId: String, planWeeks: List<com.plan.app.core.model.PlanWeek>): Int {
        var completed = 0
        for (w in planWeeks) {
            for (l in w.lessons) {
                if (isLessonCompleted(classroomId, w.weekNumber, l.lessonIndex)) {
                    completed += l.hours
                }
            }
        }
        return completed
    }

    fun saveNote(
        classroomId: String,
        weekNumber: Int,
        lessonIndex: Int?,
        text: String,
        type: NoteType = NoteType.TEXT,
        drawingStrokes: List<DrawingStroke> = emptyList()
    ): TeacherNote {
        val current = _notesList.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        }

        val note: TeacherNote
        if (existingIndex >= 0) {
            note = current[existingIndex].copy(
                text = text,
                noteType = type,
                drawingPoints = if (type == NoteType.DRAWING || type == NoteType.HYBRID) drawingStrokes else current[existingIndex].drawingPoints,
                updatedAt = System.currentTimeMillis()
            )
            current[existingIndex] = note
        } else {
            note = TeacherNote(
                id = "note-" + System.currentTimeMillis(),
                classroomId = classroomId,
                weekNumber = weekNumber,
                lessonIndex = lessonIndex,
                text = text,
                noteType = type,
                drawingPoints = drawingStrokes,
                updatedAt = System.currentTimeMillis()
            )
            current.add(note)
        }
        _notesList.value = current
        persistData()
        return note
    }

    fun getNote(classroomId: String, weekNumber: Int, lessonIndex: Int?): TeacherNote? {
        return _notesList.value.firstOrNull {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        }
    }

    fun clearAllForTesting() {
        _progressList.value = emptyList()
        _notesList.value = emptyList()
        try {
            getStorageFile("progress.json")?.delete()
            getStorageFile("notes.json")?.delete()
        } catch (e: Exception) {}
    }

    private fun getStorageFile(filename: String): File? {
        val dir = context?.filesDir ?: File("build/tmp/test_storage")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, filename)
    }

    private fun persistData() {
        try {
            getStorageFile("classrooms.json")?.writeText(json.encodeToString(_classrooms.value))
            getStorageFile("progress.json")?.writeText(json.encodeToString(_progressList.value))
            getStorageFile("notes.json")?.writeText(json.encodeToString(_notesList.value))
        } catch (e: Exception) {
            // Memory state preserved even if file write fails
        }
    }

    private fun loadLocalData() {
        try {
            val classFile = getStorageFile("classrooms.json")
            if (classFile?.exists() == true) {
                _classrooms.value = json.decodeFromString(classFile.readText())
            }
            val progFile = getStorageFile("progress.json")
            if (progFile?.exists() == true) {
                _progressList.value = json.decodeFromString(progFile.readText())
            }
            val notesFile = getStorageFile("notes.json")
            if (notesFile?.exists() == true) {
                _notesList.value = json.decodeFromString(notesFile.readText())
            }
        } catch (e: Exception) {
            // fallback to defaults
        }
    }
}
