package com.plan.app.core.data

import android.content.Context
import android.util.AtomicFile
import android.os.Trace
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.DrawingStroke
import com.plan.app.core.model.FontScalePreference
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
import java.util.concurrent.Executors

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
    val fontScalePercent: Int = FontScalePreference.DEFAULT_PERCENT,
    val weeksByClassroom: Map<String, Int> = emptyMap()
)

class TeacherRepository(private val context: Context? = null) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val defaults = listOf(
        Classroom("cls-11a", "11-A (Anadolu)", 11, "Anadolu Lisesi", "2026-2027", "Türk Dili ve Edebiyatı", true),
        Classroom("cls-11b", "11-B (Anadolu)", 11, "Anadolu Lisesi", "2026-2027", "Türk Dili ve Edebiyatı"),
        Classroom("cls-11fen", "11-Fen (Fen Lisesi)", 11, "Fen Lisesi", "2026-2027", "Türk Dili ve Edebiyatı")
    )

    private val diskWriter = if (context != null) Executors.newSingleThreadExecutor { task ->
        Thread(task, "plan-personal-data-writer").apply { isDaemon = true }
    } else null
    private var scheduledRevision = 0L
    @Volatile private var persistedRevision = 0L
    private var initialLoadWarning: String? = null
    // When all stored copies are unreadable, remain read-only until an explicit restore.
    // Never let normal screen interactions overwrite the only damaged original.
    private var initialRecoveryRequired = false
    @Volatile private var snapshot = loadState()
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
    private val _fontScalePercent = MutableStateFlow(snapshot.fontScalePercent)
    val fontScalePercent: StateFlow<Int> = _fontScalePercent.asStateFlow()
    private val _storageError = MutableStateFlow<String?>(initialLoadWarning)
    val storageError: StateFlow<String?> = _storageError.asStateFlow()
    private val _recoveryRequired = MutableStateFlow(initialRecoveryRequired)
    val recoveryRequired: StateFlow<Boolean> = _recoveryRequired.asStateFlow()

    @Synchronized
    private fun change(next: TeacherSnapshot) {
        if (_recoveryRequired.value) {
            _storageError.value = "Veri kurtarma gerekli: eski dosyalar korunuyor. Önce yedeği geri yükleyin."
            return
        }
        try {
            if (context == null) persist(next)
            snapshot = next
            _classrooms.value = next.classrooms
            _progressList.value = next.progress
            _notesList.value = next.notes
            _activeClassroomId.value = next.activeClassroomId
            _activeWeekNumber.value = next.activeWeekNumber
            _viewMode.value = next.viewMode
            _darkMode.value = next.darkMode
            _fontScalePercent.value = next.fontScalePercent
            if (diskWriter == null) {
                _storageError.value = null
            } else {
                val revision = ++scheduledRevision
                diskWriter.execute {
                    try {
                        persist(next)
                        persistedRevision = revision
                        _storageError.value = null
                    } catch (error: Exception) {
                        _storageError.value = "Kişisel veriler kaydedilemedi: " +
                            (error.message ?: "Depolama hatası")
                    }
                }
            }
        } catch (error: Exception) {
            _storageError.value = "Kişisel veriler kaydedilemedi: " + (error.message ?: "Depolama hatası")
            throw IllegalStateException(_storageError.value, error)
        }
    }

    /** Durability barrier. Ordinary edits are always queued off the UI thread. */
    fun flushPendingWrites(): Boolean {
        if (_recoveryRequired.value) return false
        val writer = diskWriter ?: return true
        val (revision, latest) = synchronized(this) { scheduledRevision to snapshot }
        if (persistedRevision >= revision) return true
        return try {
            writer.submit {
                if (persistedRevision < revision) {
                    persist(latest)
                    persistedRevision = revision
                    _storageError.value = null
                }
            }.get()
            persistedRevision >= revision
        } catch (error: Exception) {
            _storageError.value = "Kişisel veriler kaydedilemedi: " +
                (error.cause?.message ?: error.message ?: "Depolama hatası")
            false
        }
    }

    @Synchronized
    fun exportBackupJson(): String =
        json.encodeToString(TeacherSnapshot.serializer(), snapshot)

    /**
     * Commit a validated restore to disk *before* publishing it to any StateFlow.
     * The serialized writer drains older writes first, preventing stale revisions
     * from overwriting a newer restore. Disk errors leave the visible state unchanged.
     */
    @Synchronized
    fun importBackupJson(content: String) {
        val imported = json.decodeFromString(TeacherSnapshot.serializer(), content)
        require(imported.version == 1) { "Desteklenmeyen yedek sürümü" }
        require(imported.classrooms.isNotEmpty()) { "Yedekte sınıf kaydı bulunamadı" }
        require(imported.classrooms.map { it.id }.distinct().size == imported.classrooms.size) {
            "Yedekte yinelenen sınıf kimlikleri var"
        }
        require(imported.classrooms.any { it.id == imported.activeClassroomId }) {
            "Etkin sınıf yedekte bulunamadı"
        }
        val restored = imported.copy(
            fontScalePercent = FontScalePreference.normalize(imported.fontScalePercent),
            viewMode = PlannerViewMode.fromSaved(imported.viewMode)
        )

        try {
            // Import is invoked from Dispatchers.IO; it may wait on disk durability.
            val writer = diskWriter
            if (writer != null) writer.submit { persist(restored) }.get()
            else persist(restored)
        } catch (error: Exception) {
            _storageError.value = "Yedek diske kaydedilemedi: " +
                (error.cause?.message ?: error.message ?: "Depolama hatası")
            throw IllegalStateException(_storageError.value, error)
        }

        snapshot = restored
        // Future normal writes start after the restore's successful disk commit.
        persistedRevision = ++scheduledRevision
        _recoveryRequired.value = false
        _classrooms.value = restored.classrooms
        _progressList.value = restored.progress
        _notesList.value = restored.notes
        _activeClassroomId.value = restored.activeClassroomId
        _activeWeekNumber.value = restored.activeWeekNumber
        _viewMode.value = restored.viewMode
        _darkMode.value = restored.darkMode
        _fontScalePercent.value = restored.fontScalePercent
        _storageError.value = null
    }

    @Synchronized
    fun selectClassroom(id: String) {
        require(snapshot.classrooms.any { it.id == id }) { "Sınıf bulunamadı: $id" }
        change(snapshot.copy(
            activeClassroomId = id,
            activeWeekNumber = snapshot.weeksByClassroom[id] ?: 1
        ))
    }

    @Synchronized
    fun selectWeek(weekNumber: Int) {
        require(weekNumber > 0)
        change(snapshot.copy(
            activeWeekNumber = weekNumber,
            weeksByClassroom = snapshot.weeksByClassroom + (snapshot.activeClassroomId to weekNumber)
        ))
    }

    @Synchronized
    fun selectViewMode(mode: PlannerViewMode) =
        change(snapshot.copy(viewMode = PlannerViewMode.fromSaved(mode)))
    @Synchronized
    fun setDarkMode(enabled: Boolean) = change(snapshot.copy(darkMode = enabled))

    @Synchronized
    fun setFontScalePercent(percent: Int) {
        require(FontScalePreference.isValid(percent)) { "Geçersiz yazı boyutu: $percent" }
        if (snapshot.fontScalePercent != percent) change(snapshot.copy(fontScalePercent = percent))
    }

    @Synchronized
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

    @Synchronized
    fun toggleLessonProgress(classroomId: String, weekNumber: Int, lessonIndex: Int): Boolean {
        val next = !isLessonCompleted(classroomId, weekNumber, lessonIndex)
        setLessonProgress(classroomId, weekNumber, lessonIndex, next)
        return next
    }

    /**
     * Swipe and checkbox actions set an explicit desired state instead of blindly
     * toggling. Repeating a gesture is safe and does not rewrite the snapshot.
     */
    @Synchronized
    fun setLessonProgress(
        classroomId: String, weekNumber: Int, lessonIndex: Int, completed: Boolean
    ): Boolean {
        require(weekNumber > 0 && lessonIndex > 0)
        val previous = snapshot.progress.firstOrNull {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        }
        if (previous?.isCompleted == completed || (previous == null && !completed)) return completed
        val newEntry = LessonProgress(
            classroomId, weekNumber, lessonIndex, completed,
            if (completed) System.currentTimeMillis() else null
        )
        val next = snapshot.progress.filterNot {
            it.classroomId == classroomId && it.weekNumber == weekNumber && it.lessonIndex == lessonIndex
        } + newEntry
        change(snapshot.copy(progress = next))
        return completed
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

    @Synchronized
    fun saveNote(
        classroomId: String,
        weekNumber: Int,
        lessonIndex: Int?,
        text: String,
        type: NoteType = NoteType.TEXT,
        drawingStrokes: List<DrawingStroke> = emptyList()
    ): TeacherNote {
        val existing = getNote(classroomId, weekNumber, lessonIndex)
        if (existing != null && existing.text == text &&
            existing.noteType == type && (type == NoteType.TEXT || existing.drawingPoints == drawingStrokes)
        ) return existing
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
        check(flushPendingWrites()) { "Pending writes must complete before clearing tests" }
        snapshot = TeacherSnapshot(classrooms = defaults)
        _recoveryRequired.value = false
        initialRecoveryRequired = false
        _classrooms.value = defaults
        _progressList.value = emptyList()
        _notesList.value = emptyList()
        _activeClassroomId.value = "cls-11a"
        _activeWeekNumber.value = 1
        _viewMode.value = PlannerViewMode.WEEK
        _darkMode.value = false
        _fontScalePercent.value = FontScalePreference.DEFAULT_PERCENT
        _storageError.value = null
        stateFile().delete()
        File(stateFile().path + ".bak").delete()
        File(stateFile().path + ".new").delete()
        File(stateFile().path + ".recovery.json").delete()
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
            // Visible in Perfetto/Android Studio system traces on a real tablet.
            // Do not use Trace in the context=null JVM test code path.
            Trace.beginSection("PlanTeacherAtomicWrite")
            try {
                // Write the recovery copy first: a failed primary commit must not
                // publish a new snapshot with a missing or stale safety copy.
                writeAtomic(File(file.path + ".recovery.json"), bytes)
                writeAtomic(file, bytes)
            } finally {
                Trace.endSection()
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

    private fun writeAtomic(file: File, bytes: ByteArray) {
        val atomic = AtomicFile(file)
        val stream = atomic.startWrite()
        try {
            stream.write(bytes)
            atomic.finishWrite(stream)
        } catch (error: Exception) {
            atomic.failWrite(stream)
            throw error
        }
    }

    private fun parseSnapshot(file: File): TeacherSnapshot {
        val raw = if (context != null) AtomicFile(file).openRead().bufferedReader().use { it.readText() }
            else file.readText()
        val parsed = json.decodeFromString(TeacherSnapshot.serializer(), raw)
        require(parsed.version == 1) { "Bilinmeyen öğretmen veri sürümü" }
        return parsed.copy(
            classrooms = parsed.classrooms.ifEmpty { defaults },
            fontScalePercent = FontScalePreference.normalize(parsed.fontScalePercent),
            viewMode = PlannerViewMode.fromSaved(parsed.viewMode)
        )
    }

    private fun loadState(): TeacherSnapshot {
        val file = stateFile()
        if (file.exists() || File(file.path + ".bak").exists() ||
            File(file.path + ".recovery.json").exists()
        ) {
            runCatching { parseSnapshot(file) }.getOrNull()?.let { return it }
            val recovery = File(file.path + ".recovery.json")
            val recovered = runCatching {
                if (recovery.isFile) parseSnapshot(recovery) else null
            }.getOrNull()
            // Do not silently erase notes from a damaged file.
            if (file.exists()) {
                // The original file is still present and never written in recovery mode.
                // A full/read-only disk must not crash the app solely because an
                // additional diagnostic copy could not be created.
                runCatching {
                    file.copyTo(File(file.path + ".corrupt-" + System.currentTimeMillis()))
                }.onFailure {
                    initialLoadWarning = "Bozuk dosya yerinde korundu; ek kopya oluşturulamadı."
                }
            }
            if (recovered != null) {
                initialLoadWarning = "Kayıt kurtarma yedeğinden açıldı; dışa aktararak yedekleyin."
                return recovered
            }
            initialLoadWarning = "Öğretmen verisi okunamadı. Hasarlı dosya korundu; yedekten geri yükleyin."
            initialRecoveryRequired = true
            return TeacherSnapshot(classrooms = defaults)
        }
        val base = file.parentFile
        fun <T> readLegacy(name: String, block: (String) -> T): T? {
            val legacy = File(base, name)
            return if (legacy.isFile) runCatching {
                block(legacy.readText(Charsets.UTF_8))
            }.onFailure {
                initialLoadWarning = "Eski veri dosyası ($name) okunamadı; özgün dosya korundu."
            }.getOrNull() else null
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
        // A malformed legacy input must not be replaced with apparently valid
        // defaults. Keep the original files and require an explicit restore.
        if (initialLoadWarning != null) initialRecoveryRequired = true
        if (context != null && !initialRecoveryRequired) persist(state)
        return state
    }
}
