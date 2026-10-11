package com.plan.app

import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.LearningOutcome
import com.plan.app.core.model.PlanLesson
import com.plan.app.core.model.PlanMetadata
import com.plan.app.core.model.PlanTheme
import com.plan.app.core.model.PlanWeek
import com.plan.app.core.model.WeekThemeRef
import com.plan.app.feature.planner.searchPlan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlanSearchTest {
    private val plan = AnnualPlan(
        metadata = PlanMetadata(
            academicYear = "2026-2027", gradeLevel = 11, courseName = "TDE",
            schoolType = "Anadolu", documentTitle = "Yıllık Plan"
        ),
        themes = listOf(PlanTheme(
            themeNumber = 1, themeCode = "TDE1", themeName = "Sözcüklerin Gücü", weekRange = listOf(1)
        )),
        weeks = listOf(
            PlanWeek(
                weekNumber = 1, month = "EYLÜL", dateRangeRaw = "14-18 Eylül",
                startDate = "2026-09-14", endDate = "2026-09-18", totalHours = 5,
                theme = WeekThemeRef(themeCode = "TDE1", themeName = "Sözcüklerin Gücü"),
                lessons = listOf(
                    PlanLesson(
                        lessonIndex = 1, topicTitle = "Şiir dili", textsAndMaterials = listOf("Bahar şiiri"),
                        learningOutcomes = listOf(LearningOutcome("TDE2.1", "Metnin ana duygusunu belirler"))
                    )
                )
            ),
            PlanWeek(
                weekNumber = 2, month = "EKİM", dateRangeRaw = "5-9 Ekim",
                startDate = "2026-10-05", endDate = "2026-10-09", totalHours = 5,
                lessons = listOf(PlanLesson(lessonIndex = 1, topicTitle = "Hikâye", learningOutcomesRaw = "Anlatıcı türleri"))
            )
        )
    )

    @Test fun searchesWeekNumbersThemeTopicTextOutcomeAndCode() {
        assertEquals(listOf(1), searchPlan(plan, "1. hafta").map { it.week.weekNumber })
        assertEquals(listOf(1), searchPlan(plan, "sözcüklerin gücü").map { it.week.weekNumber })
        assertEquals(listOf(1), searchPlan(plan, "şiir dili").map { it.week.weekNumber })
        assertEquals(listOf(1), searchPlan(plan, "bahar şiiri").map { it.week.weekNumber })
        assertEquals(listOf(1), searchPlan(plan, "TDE2.1").map { it.week.weekNumber })
        assertEquals(listOf(1), searchPlan(plan, "ana duygusunu").map { it.week.weekNumber })
    }

    @Test fun termsMustAllMatchAndBlankOrMissingPlanHasNoResults() {
        assertTrue(searchPlan(plan, "şiir hikâye").isEmpty())
        assertTrue(searchPlan(plan, "   ").isEmpty())
        assertTrue(searchPlan(null, "şiir").isEmpty())
    }
}
