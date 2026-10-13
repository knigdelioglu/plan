package com.plan.app

import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class TeacherStateRecoveryTest {
    @Test
    fun damagedPrimaryUsesExistingRecoveryCopy() {
        val original = TeacherRepository(context = null)
        original.clearAllForTesting()
        try {
            original.saveNote("cls-11a", 5, null, "Kurtarılacak not")
            val mainFile = File("build/tmp/test_storage/teacher-state.json")
            File(mainFile.path + ".recovery.json").writeText(original.exportBackupJson())
            mainFile.writeText("invalid JSON data")
            val restored = TeacherRepository(context = null)
            assertEquals("Kurtarılacak not", restored.getNote("cls-11a", 5, null)?.text)
            assertNotNull(restored.storageError.value)
        } finally {
            original.clearAllForTesting()
        }
    }
}
