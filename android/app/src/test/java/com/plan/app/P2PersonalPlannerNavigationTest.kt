package com.plan.app

import com.plan.app.core.data.TeacherRepository
import com.plan.app.core.model.PlannerViewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class P2PersonalPlannerNavigationTest {
    @Test
    fun onlyFourPersonalPlannerSectionsArePublic() {
        assertEquals(
            listOf("Haftalık", "Aylık", "Temalar", "İlerleme"),
            PlannerViewMode.visibleModes.map { it.title }
        )
        listOf(PlannerViewMode.YEAR, PlannerViewMode.COMPARISON,
            PlannerViewMode.VALIDATION, PlannerViewMode.FAQ).forEach {
            assertFalse(PlannerViewMode.visibleModes.contains(it))
            assertEquals(PlannerViewMode.WEEK, PlannerViewMode.fromSaved(it))
        }
    }

    @Test
    fun legacySavedViewIsMigratedWithoutLosingTeachersNotes() {
        val repository = TeacherRepository(context = null)
        repository.clearAllForTesting()
        try {
            repository.saveNote("cls-11a", 4, null, "Eski öğretmen notu")
            repository.selectViewMode(PlannerViewMode.MONTH)
            val file = File("build/tmp/test_storage/teacher-state.json")
            assertTrue(file.isFile)
            val raw = file.readText()
            val previousView = "\"viewMode\":\"MONTH\""
            val legacyView = "\"viewMode\":\"VALIDATION\""
            assertTrue(raw.contains(previousView))
            file.writeText(raw.replace(previousView, legacyView))

            val restored = TeacherRepository(context = null)
            assertEquals(PlannerViewMode.WEEK, restored.viewMode.value)
            assertEquals("Eski öğretmen notu", restored.getNote("cls-11a", 4, null)?.text)
            restored.selectViewMode(PlannerViewMode.COMPARISON)
            assertEquals(PlannerViewMode.WEEK, restored.viewMode.value)
            assertEquals("Eski öğretmen notu", restored.getNote("cls-11a", 4, null)?.text)
        } finally {
            repository.clearAllForTesting()
        }
    }
}
