package com.plan.app

import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcDarkColors
import com.plan.app.core.design.theme.ArcLightColors
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.model.PlanLesson
import com.plan.app.core.model.PlanWeek
import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.unit.dp

class ArcDesignAndDomainTest {

    @Test
    fun testArcMotionTokens() {
        assertEquals(120, ArcMotionTokens.Duration.Instant)
        assertEquals(160, ArcMotionTokens.Duration.Fast)
        assertEquals(180, ArcMotionTokens.Duration.Exit)
        assertEquals(240, ArcMotionTokens.Duration.Standard)
        assertEquals(480, ArcMotionTokens.Duration.Considered)
        assertEquals(580, ArcMotionTokens.Duration.Spring)

        assertEquals(16L, ArcMotionTokens.Stagger.CharMillis)
        assertEquals(40L, ArcMotionTokens.Stagger.WordMillis)
        assertEquals(80L, ArcMotionTokens.Stagger.LineMillis)
        assertEquals(35L, ArcMotionTokens.Stagger.ItemMillis)

        assertNotNull(ArcMotionTokens.Easing.Enter)
        assertNotNull(ArcMotionTokens.Easing.Exit)
        assertNotNull(ArcMotionTokens.Easing.Standard)
        assertNotNull(ArcMotionTokens.Easing.InOut)

        assertNotNull(ArcMotionTokens.Springs.Snappy)
        assertNotNull(ArcMotionTokens.Springs.Smooth)
        assertNotNull(ArcMotionTokens.Springs.Morph)
        assertNotNull(ArcMotionTokens.Springs.Responsive)
        assertNotNull(ArcMotionTokens.Springs.Gentle)
    }

    @Test
    fun testArcThemeTokens() {
        assertFalse(ArcLightColors.isDark)
        assertTrue(ArcDarkColors.isDark)

        assertEquals(4.dp, ArcSpacing.space1)
        assertEquals(8.dp, ArcSpacing.space2)
        assertEquals(16.dp, ArcSpacing.space4)
        assertEquals(24.dp, ArcSpacing.space6)
        assertEquals(32.dp, ArcSpacing.space8)
        assertEquals(48.dp, ArcSpacing.space12)

        assertNotNull(ArcRadius.control)
        assertNotNull(ArcRadius.panel)
        assertNotNull(ArcRadius.surface)
        assertNotNull(ArcRadius.pill)
    }

    @Test
    fun testHoursAccountingCalculation() {
        val teacherRepo = TeacherRepository(context = null)
        teacherRepo.clearAllForTesting()

        val sampleWeeks = listOf(
            PlanWeek(
                weekNumber = 1,
                month = "EYLÜL",
                dateRangeRaw = "14-18 Eylül",
                startDate = "2026-09-14",
                endDate = "2026-09-18",
                totalHours = 5,
                lessons = listOf(PlanLesson(lessonIndex = 1, hours = 5))
            ),
            PlanWeek(
                weekNumber = 7,
                month = "EKİM",
                dateRangeRaw = "26-30 Ekim",
                startDate = "2026-10-26",
                endDate = "2026-10-30",
                totalHours = 5,
                lessons = listOf(
                    PlanLesson(lessonIndex = 1, hours = 3),
                    PlanLesson(lessonIndex = 2, hours = 2)
                )
            )
        )

        val cid = "cls-test"
        assertEquals(0, teacherRepo.getCompletedHours(cid, sampleWeeks))

        // Complete lesson 1 of week 1 (5 hours)
        teacherRepo.toggleLessonProgress(cid, 1, 1)
        assertEquals(5, teacherRepo.getCompletedHours(cid, sampleWeeks))

        // Complete lesson 1 of week 7 (3 hours)
        teacherRepo.toggleLessonProgress(cid, 7, 1)
        assertEquals(8, teacherRepo.getCompletedHours(cid, sampleWeeks))

        // Complete lesson 2 of week 7 (2 hours)
        teacherRepo.toggleLessonProgress(cid, 7, 2)
        assertEquals(10, teacherRepo.getCompletedHours(cid, sampleWeeks))

        // Uncheck lesson 1 of week 1
        teacherRepo.toggleLessonProgress(cid, 1, 1)
        assertEquals(5, teacherRepo.getCompletedHours(cid, sampleWeeks))
    }
}
