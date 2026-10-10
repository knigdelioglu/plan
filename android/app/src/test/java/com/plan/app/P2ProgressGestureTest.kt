package com.plan.app

import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Distinguishes a desired completion state from a dangerous toggle gesture. */
class P2ProgressGestureTest {
    @Test
    fun repeatSwipesAreIdempotentAndDifferentClassesRemainIndependent() {
        val repo = TeacherRepository(context = null)
        repo.clearAllForTesting()
        try {
            assertFalse(repo.isLessonCompleted("cls-11a", 3, 1))
            assertTrue(repo.setLessonProgress("cls-11a", 3, 1, true))
            val snapshot = File("build/tmp/test_storage/teacher-state.json").readText()
            assertTrue(repo.setLessonProgress("cls-11a", 3, 1, true))
            assertEquals(snapshot, File("build/tmp/test_storage/teacher-state.json").readText())
            assertTrue(repo.isLessonCompleted("cls-11a", 3, 1))
            assertFalse(repo.isLessonCompleted("cls-11b", 3, 1))

            assertFalse(repo.setLessonProgress("cls-11a", 3, 1, false))
            val cleared = File("build/tmp/test_storage/teacher-state.json").readText()
            assertFalse(repo.setLessonProgress("cls-11a", 3, 1, false))
            assertEquals(cleared, File("build/tmp/test_storage/teacher-state.json").readText())
            assertFalse(repo.isLessonCompleted("cls-11a", 3, 1))
        } finally {
            repo.clearAllForTesting()
        }
    }

    @Test
    fun checkboxStateSetterAndLegacyToggleAgree() {
        val repo = TeacherRepository(context = null)
        repo.clearAllForTesting()
        try {
            repo.setLessonProgress("cls-11a", 2, 1, true)
            assertFalse(repo.toggleLessonProgress("cls-11a", 2, 1))
            assertTrue(repo.toggleLessonProgress("cls-11a", 2, 1))
        } finally {
            repo.clearAllForTesting()
        }
    }
}
