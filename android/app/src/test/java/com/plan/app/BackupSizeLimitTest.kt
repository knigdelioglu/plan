package com.plan.app

import com.plan.app.feature.planner.readLimitedBackup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.StringReader

class BackupSizeLimitTest {
    @Test fun acceptsBackupWithinLimit() {
        assertEquals("{}", StringReader("{}").use { readLimitedBackup(it, maxCharacters = 8) })
    }

    @Test fun rejectsBackupLargerThanConfiguredBound() {
        assertThrows(IllegalStateException::class.java) {
            StringReader("123456789").use { readLimitedBackup(it, maxCharacters = 8) }
        }
    }
}
