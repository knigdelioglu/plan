package com.plan.app

import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeacherBackupRoundTripTest {
    @Test fun importedBackupRestoresClassNotesAndProgress() {
        val repository = TeacherRepository(context = null)
        repository.clearAllForTesting()
        try {
            repository.saveNote("cls-11a", 2, null, "Öğretmen notu")
            repository.setLessonProgress("cls-11b", 4, 1, true)
            val backup = repository.exportBackupJson()
            repository.clearAllForTesting()
            repository.importBackupJson(backup)
            assertEquals("Öğretmen notu", repository.getNote("cls-11a", 2, null)?.text)
            assertTrue(repository.isLessonCompleted("cls-11b", 4, 1))
        } finally {
            repository.clearAllForTesting()
        }
    }
}
