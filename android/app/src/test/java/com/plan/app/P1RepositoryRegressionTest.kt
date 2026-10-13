package com.plan.app

import com.plan.app.core.data.PlanRepository
import com.plan.app.core.data.TeacherRepository
import com.plan.app.core.model.Classroom
import com.plan.app.core.model.FontScalePreference
import com.plan.app.core.model.PlannerViewMode
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class P1RepositoryRegressionTest {
    @Test
    fun dynamicallyDiscoversPlansInNewFoldersWithoutHardcodedSchoolMapping() {
        val root = Files.createTempDirectory("plans-catalog").toFile()
        try {
            val source = File("../../plans/2026-2027/turk-dili-ve-edebiyati/11/fen-lisesi/annual-plan.json")
            assertTrue(source.isFile)
            val nested = File(root, "future-year/another-course/11/fen-lisesi/annual-plan.json")
            nested.parentFile.mkdirs()
            source.copyTo(nested)
            val repository = PlanRepository(plansBaseDir = root)
            val catalog = repository.availablePlans()
            assertEquals(1, catalog.size)
            assertEquals("future-year/another-course/11/fen-lisesi", catalog.single().pathSegment)
            val classroom = Classroom(
                "test", "Fen 11", 11, "Fen Lisesi", "2026-2027", "Türk Dili ve Edebiyatı"
            )
            assertEquals(37, repository.loadAnnualPlan(classroom).weeks.size)
            assertThrows(IllegalStateException::class.java) {
                repository.loadAnnualPlan("Sosyal Bilimler Lisesi")
            }
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun persistsClassNotesProgressAndViewPreferencesAcrossRepositoryInstances() {
        val teacher = TeacherRepository(context = null)
        teacher.clearAllForTesting()
        try {
            teacher.saveNote("cls-11a", 3, null, "Ödev verildi")
            teacher.saveNote("cls-11b", 3, null, "Farklı sınıf")
            teacher.toggleLessonProgress("cls-11a", 3, 1)
            teacher.selectWeek(5)
            teacher.selectViewMode(PlannerViewMode.MONTH)
            teacher.setDarkMode(true)
            teacher.setFontScalePercent(130)
            teacher.selectClassroom("cls-11b")
            teacher.selectWeek(7)

            val restored = TeacherRepository(context = null)
            assertEquals("cls-11b", restored.activeClassroomId.value)
            assertEquals(7, restored.activeWeekNumber.value)
            assertEquals(PlannerViewMode.MONTH, restored.viewMode.value)
            assertTrue(restored.darkMode.value)
            assertEquals(130, restored.fontScalePercent.value)
            assertEquals("Ödev verildi", restored.getNote("cls-11a", 3, null)?.text)
            assertEquals("Farklı sınıf", restored.getNote("cls-11b", 3, null)?.text)
            assertTrue(restored.isLessonCompleted("cls-11a", 3, 1))
            assertFalse(restored.isLessonCompleted("cls-11b", 3, 1))
            restored.selectClassroom("cls-11a")
            assertEquals(5, restored.activeWeekNumber.value)
        } finally {
            teacher.clearAllForTesting()
        }
    }

    @Test
    fun oldTeacherSnapshotsReceiveRecommendedFontSizeAndInvalidValuesAreRejected() {
        val teacher = TeacherRepository(context = null)
        teacher.clearAllForTesting()
        try {
            val stateFile = File("build/tmp/test_storage/teacher-state.json")
            stateFile.writeText("""{"version":1,"darkMode":true}""")
            val restored = TeacherRepository(context = null)
            assertEquals(FontScalePreference.DEFAULT_PERCENT, restored.fontScalePercent.value)
            assertTrue(restored.darkMode.value)
            assertThrows(IllegalArgumentException::class.java) {
                restored.setFontScalePercent(141)
            }
            restored.setFontScalePercent(90)
            assertEquals(90, TeacherRepository(context = null).fontScalePercent.value)
        } finally {
            teacher.clearAllForTesting()
        }
    }

    @Test
    fun migratesOldProgressAndNotesWithoutDeletingSourceFiles() {
        val teacher = TeacherRepository(context = null)
        teacher.clearAllForTesting()
        val root = File("build/tmp/test_storage")
        val legacyProgress = File(root, "progress.json")
        val legacyNotes = File(root, "notes.json")
        try {
            legacyProgress.writeText("""[{"classroomId":"cls-11a","weekNumber":2,"lessonIndex":1,"isCompleted":true}]""")
            legacyNotes.writeText("""[{"id":"legacy-note","classroomId":"cls-11a","weekNumber":2,"text":"Eski not"}]""")
            val migrated = TeacherRepository(context = null)
            assertTrue(migrated.isLessonCompleted("cls-11a", 2, 1))
            assertEquals("Eski not", migrated.getNote("cls-11a", 2, null)?.text)
            migrated.selectWeek(3)
            assertTrue(File(root, "teacher-state.json").isFile)
            assertTrue(legacyProgress.isFile)
            assertTrue(legacyNotes.isFile)
        } finally {
            teacher.clearAllForTesting()
        }
    }
}
