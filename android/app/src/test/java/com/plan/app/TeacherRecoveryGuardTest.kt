package com.plan.app

import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TeacherRecoveryGuardTest {
    @Test
    fun damagedStateWithoutRecoveryIsReadOnlyUntilValidRestore() {
        val original = TeacherRepository(context = null)
        original.clearAllForTesting()
        try {
            original.saveNote("cls-11a", 8, null, "Kritik öğretmen notu")
            val validBackup = original.exportBackupJson()
            val primary = File("build/tmp/test_storage/teacher-state.json")
            File(primary.path + ".recovery.json").delete()
            primary.writeText("damaged-snapshot")

            val blocked = TeacherRepository(context = null)
            assertTrue(blocked.recoveryRequired.value)
            assertNotNull(blocked.storageError.value)
            blocked.selectWeek(13)
            blocked.saveNote("cls-11a", 8, null, "Üzerine yazılamaz")
            assertEquals("damaged-snapshot", primary.readText())
            assertFalse(blocked.flushPendingWrites())

            blocked.importBackupJson(validBackup)
            assertFalse(blocked.recoveryRequired.value)
            assertEquals("Kritik öğretmen notu", blocked.getNote("cls-11a", 8, null)?.text)
            assertTrue(blocked.flushPendingWrites())
        } finally {
            original.clearAllForTesting()
        }
    }

    @Test
    fun recoveryCopyWorksEvenWhenPrimaryFileIsMissing() {
        val original = TeacherRepository(context = null)
        original.clearAllForTesting()
        try {
            original.saveNote("cls-11a", 2, null, "Kurtarma kopyasındaki not")
            val primary = File("build/tmp/test_storage/teacher-state.json")
            File(primary.path + ".recovery.json").writeText(original.exportBackupJson())
            primary.delete()

            val restored = TeacherRepository(context = null)
            assertFalse(restored.recoveryRequired.value)
            assertEquals("Kurtarma kopyasındaki not", restored.getNote("cls-11a", 2, null)?.text)
        } finally {
            original.clearAllForTesting()
        }
    }

    @Test
    fun invalidRestoreDoesNotChangeCurrentlyVisibleData() {
        val original = TeacherRepository(context = null)
        original.clearAllForTesting()
        try {
            original.saveNote("cls-11a", 3, null, "Korunan içerik")
            try {
                original.importBackupJson("{}")
                throw AssertionError("Missing classes should be rejected")
            } catch (_: IllegalArgumentException) { }
            assertEquals("Korunan içerik", original.getNote("cls-11a", 3, null)?.text)
        } finally {
            original.clearAllForTesting()
        }
    }
}
