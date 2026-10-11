package com.plan.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.input.key.Key
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.AnnualPlan
import com.plan.app.core.model.LearningOutcome
import com.plan.app.core.model.PlanLesson
import com.plan.app.core.model.PlanMetadata
import com.plan.app.core.model.PlanWeek
import com.plan.app.feature.planner.PlanSearchPanel
import com.plan.app.feature.planner.searchPlan
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PlanSearchPanelTest {
    @get:Rule val rule = createComposeRule()

    private val plan = AnnualPlan(
        metadata = PlanMetadata(
            academicYear = "2026-2027", gradeLevel = 11, courseName = "TDE",
            schoolType = "Anadolu", documentTitle = "Yıllık Plan"
        ),
        weeks = listOf(
            PlanWeek(
                weekNumber = 3, month = "EKİM", dateRangeRaw = "12-16 Ekim",
                startDate = "2026-10-12", endDate = "2026-10-16", totalHours = 5,
                lessons = listOf(
                    PlanLesson(
                        topicTitle = "Şiir dili",
                        learningOutcomes = listOf(LearningOutcome("TDE2.1", "Metni yorumlar"))
                    )
                )
            )
        )
    )

    @Test fun searchingByOutcomeCodeShowsWeekAndSelectingItOpensThatWeek() {
        var query by mutableStateOf("")
        var selectedWeek = 0
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                // The actual planner hosts search in a scrollable workspace; mirror
                // that contract so the IME cannot make a valid result untappable.
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PlanSearchPanel(
                        query = query,
                        results = searchPlan(plan, query),
                        onQueryChange = { query = it },
                        onSelectWeek = { selectedWeek = it }
                    )
                }
            }
        }

        rule.onNodeWithContentDescription("Ara").performClick()
        rule.onNode(hasSetTextAction()).performTextInput("TDE2.1")
        val result = rule.onNodeWithTag("plan-search-week-3")
        result.assertExists()
        result.performScrollTo()
        result.assertIsDisplayed()
        rule.onNodeWithText("3. Hafta · EKİM").assertIsDisplayed()
        rule.onNodeWithTag("plan-search-week-3").performClick()
        rule.runOnIdle { assertEquals(3, selectedWeek) }
    }

    @Test fun noMatchesShowsClearActionAndClearingRemovesEmptyState() {
        var query by mutableStateOf("eşleşme-yok")
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                PlanSearchPanel(
                    query = query,
                    results = searchPlan(plan, query),
                    onQueryChange = { query = it },
                    onSelectWeek = {}
                )
            }
        }

        rule.onNodeWithTag("plan-search-empty").assertIsDisplayed()
        rule.onNodeWithText("Aramayı Temizle").performClick()
        rule.onNodeWithTag("plan-search-empty").assertDoesNotExist()
    }

    @Test fun escapeClosesSearchAndClearsTheQuery() {
        var query by mutableStateOf("")
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                PlanSearchPanel(
                    query = query,
                    results = searchPlan(plan, query),
                    onQueryChange = { query = it },
                    onSelectWeek = {}
                )
            }
        }
        rule.onNodeWithContentDescription("Ara").performClick()
        rule.onNode(hasSetTextAction()).performTextInput("TDE2.1")
        rule.onNode(hasSetTextAction()).performKeyInput { pressKey(Key.Escape) }
        rule.onNodeWithContentDescription("Ara").assertIsDisplayed()
        rule.onNodeWithTag("plan-search-results").assertDoesNotExist()
    }
}
