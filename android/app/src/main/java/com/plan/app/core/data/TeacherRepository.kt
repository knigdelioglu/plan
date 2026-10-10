package com.plan.app.core.data

import android.content.Context
import android.util.AtomicFile
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.DrawingStroke
import com.plan.app.core.model.LessonProgress
import com.plan.app.core.model.NoteType
import com.plan.app.core.model.PlanWeek
import com.plan.app.core.model.PlannerViewMode
import com.plan.app.core.model.TeacherNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * One atomic snapshot for classrooms, progress, notes and UI preferences.
 * Existing legacy JSON files are imported once and never deleted.
 */
@Serializable
private data class TeacherSnapshot(
    val version: Int = 1,
    val classrooms: List<Classroom> = emptyList(),
    val progress: List<LessonProgress> = emptyList(),
    val notes: List<TeacherNote> = emptyList(),
    val activeClassroomId: String = "cls-11a",
    val activeWeekNumber: Int = 1,
    val viewMode: PlannerViewMode = PlannerViewMode.WEEK,
    val darkMode: Boolean = false,
    val weeksByClassroom: Map<String, Int> = emptyMap()
)

class TeacherRepository(private val context: Context? = null) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val defaults = listOf(
        Classroom("cls-11a", "11-A (Anadolu)", 11, "Anadolu Lisesi", "2026-2027", "Türk Dili ve Edebiyatı", true),
        Classroom("cls-11b", "11-B (Anadolu)", 11, "Anadolu Lisesi", "2026-2027", "Türk Dili ve Edebiyatı"),
        Classroom("cls-11fen", "11-Fen (Fen Lisesi)", 11, "Fen Lisesi", "2026-2027", "Türk Dili ve Edebiyatı")
    )

    private var snapshot = loadState()
    private val _classrooms = MutableStateFlow(snapshot.classrooms)
    val classrooms: StateFlow<List<Classroom>> = _classrooms.asStateFlow()
    private val _progressList = MutableStateFlow(snapshot.progress)
    val progressList: StateFlow<List<LessonProgress>> = _progressList.asStateFlow()
    private val _notesList = MutableStateFlow(snapshot.notes)
    val notesList: StateFlow<List<TeacherNote>> = _notesList.asStateFlow()
    private val _activeClassroomId = MutableStateFlow(snapshot.activeClassroomId)
    val activeClassroomId: StateFlow<String> = _activeClassroomId.asStateFlow()
    private val _activeWeekNumber = MutableStateFlow(snapshot.activeWeekNumber)
    val activeWeekNumber: StateFlow<Int> = _activeWeekNumber.asStateFlow()
    private val _viewMode = MutableStateFlow(snapshot.viewMode)
    val viewMode: StateFlow<PlannerViewMode> = _viewMode.asStateFlow()
    private val _darkMode = MutableStateFlow(snapshot.darkMode)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()
    private val _storageError = MutableStateFlow<String?>(null)
    val storageError: StateFlow<String?> = _storageError.asStateFlow()

    @Synchronized
    private fun change(next: TeacherSnapshot) {
        try {
            persist(next)
            snapshot = next
            _classrooms.value = next.classrooms
            _progressList.value = next.progress
            _notesList.value = next.notes
            _activeClassroomId.value = next.activeClassroomId
            _activeWeekNumber.value = next.activeWeekNumber
            _viewMode.value = next.viewMode
            _darkMode.value = next.darkMode
            _storageError.value = null
        } catch (error: Exception) {
            _storageError.value = "Kişisel veriler kaydedilemedi: " + (error.message ?: "Depolama hatası")
            throw IllegalStateException(_storageError.value, error)
        }
    }

    fun selectClassroom(id: String) {
        require(snapshot.classrooms.any { it.id == id }) { "Sınıf bulunamadı: $id" }
        change(snapshot.copy(
            activeClassroomId = id,
            activeWeekNumber = snapshot.weeksByClassroom[id] ?: 1
        ))
    }

    fun selectWeek(weekNumber: Int) {
        require(weekNumber > 0)
        change(snapshot.copy(
            activeWeekNumber = weekNumber,
            weeksByClassroom = snapshot.weeksByClassroom + (snapshot.activeClassroomId to weekNumber)
        ))
    }

    fun selectViewMode(mode: PlannerViewMode) =
        change(snapshot.copy(viewMode = PlannerViewMode.fromSaved(mode)))
    fun setDarkMode(enabled: Boolean) = change(snapshot.copy(darkMode = enabled))

    fun addClassroom(
        name: String,
        schoolType: String,
        gradeLevel: Int = 11,
        academicYear: String = "2026-2027",
        courseName: String = "Türk Dili ve Edebiyatı"
    ): Classroom {
        val classroom = Classroom(
            id = "cls-" + java.util.UUID.randomUUID(),
            name = name, gradeLevel = gradeLevel, schoolType = schoolType,
            academicYear = academicYear, courseName = courseName
        )
        change(snapshot.copy(classrooms = snapshot.classrooms + classroom))
        return classroom
    }

    fun toggleLessonProgress(classroomId: String, weekNumber: Int, lessonIndex: Int): Boolean {
        val existing = snapshot.progress.firstOrNull {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        }
        val newState = existing?.isCompleted != true
        val newEntry = LessonProgress(
            classroomId, weekNumber, lessonIndex, newState,
            if (newState) System.currentTimeMillis() else null
        )
        val next = snapshot.progress.filterNot {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        } + newEntry
        change(snapshot.copy(progress = next))
        return newState
    }

    fun isLessonCompleted(classroomId: String, weekNumber: Int, lessonIndex: Int): Boolean =
        snapshot.progress.any {
            it.classroomId == classroomId && it.weekNumber == weekNumber &&
                it.lessonIndex == lessonIndex && it.isCompleted
        }

    fun getCompletedHours(classroomId: String, planWeeks: List<PlanWeek>): Int =
        planWeeks.sumOf { week ->
            week.lessons.filter {
                isLessonCompleted(classroomId, week.weekNumber, it.lessonIndex)
            }.sumOf { it.hours }
        }

    fun saveNote(
        classroomId: String,
        weekNumber: Int,
        lessonIndex: Int?,
        text: String,
        type: NoteType = NoteType.TEXT,
        drawingStrokes: List<DrawingStroke> = emptyList()
    ): TeacherNote {
        val existing = getNote(classroomId, weekNumber, lessonIndex)
        val note = (existing ?: TeacherNote(
            id = "note-" + java.util.UUID.randomUUID(),
            classroomId = classroomId,
            weekNumber = weekNumber,
            lessonIndex = lessonIndex
        )).copy(
            text = text,
            noteType = type,
            drawingPoints = if (type == NoteType.TEXT && existing != null)
                existing.drawingPoints else drawingStrokes,
            updatedAt = System.currentTimeMillis()
        )
        val next = snapshot.notes.filterNot {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        } + note
        change(snapshot.copy(notes = next))
        return note
    }

    fun getNote(classroomId: String, weekNumber: Int, lessonIndex: Int?): TeacherNote? =
        snapshot.notes.firstOrNull {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        }

    fun clearAllForTesting() {
        snapshot = TeacherSnapshot(classrooms = defaults)
        _classrooms.value = defaults
        _progressList.value = emptyList()
        _notesList.value = emptyList()
        _activeClassroomId.value = "cls-11a"
        _activeWeekNumber.value = 1
        _viewMode.value = PlannerViewMode.WEEK
        _darkMode.value = false
        stateFile().delete()
        File(stateFile().path + ".bak").delete()
        File(stateFile().path + ".new").delete()
        listOf("progress.json", "notes.json").forEach { File(stateFile().parentFile, it).delete() }
    }

    private fun stateFile(): File {
        val base = context?.filesDir ?: File("build/tmp/test_storage")
        if (!base.isDirectory) check(base.mkdirs() || base.isDirectory)
        return File(base, "teacher-state.json")
    }

    private fun persist(state: TeacherSnapshot) {
        val file = stateFile()
        val bytes = json.encodeToString(TeacherSnapshot.serializer(), state).toByteArray(Charsets.UTF_8)
        if (context != null) {
            val atomic = AtomicFile(file)
            val stream = atomic.startWrite()
            try {
                stream.write(bytes)
                atomic.finishWrite(stream)
            } catch (error: Exception) {
                atomic.failWrite(stream)
                throw error
            }
        } else {
            val tmp = File(file.path + ".new")
            tmp.writeBytes(bytes)
            try {
                Files.move(tmp.toPath(), file.toPath(),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (error: java.nio.file.AtomicMoveNotSupportedException) {
                Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun loadState(): TeacherSnapshot {
        val file = stateFile()
        if (file.exists()) {
            val raw = if (context != null) AtomicFile(file).openRead().bufferedReader().use { it.readText() }
                else file.readText()
            val parsed = json.decodeFromString(TeacherSnapshot.serializer(), raw)
            require(parsed.version == 1) { "Bilinmeyen öğretmen veri sürümü" }
            return parsed.copy(
                classrooms = parsed.classrooms.ifEmpty { defaults },
                viewMode = PlannerViewMode.fromSaved(parsed.viewMode)
            )
        }
        val base = file.parentFile
        fun <T> readLegacy(name: String, block: (String) -> T): T? {
            val legacy = File(base, name)
            return if (legacy.isFile) block(legacy.readText(Charsets.UTF_8)) else null
        }
        val classrooms = readLegacy("classrooms.json") {
            json.decodeFromString<List<Classroom>>(it)
        }?.ifEmpty { defaults } ?: defaults
        val progress = readLegacy("progress.json") {
            json.decodeFromString<List<LessonProgress>>(it)
        } ?: emptyList()
        val notes = readLegacy("notes.json") {
            json.decodeFromString<List<TeacherNote>>(it)
        } ?: emptyList()
        val state = TeacherSnapshot(
            classrooms = classrooms,
            progress = progress,
            notes = notes,
            activeClassroomId = classrooms.firstOrNull()?.id ?: "cls-11a"
        )
        if (context != null) persist(state)
        return state
    }
}
