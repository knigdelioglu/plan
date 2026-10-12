package com.plan.app

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.plan.app.feature.planner.PlannerWindowMode
import com.plan.app.feature.planner.PlannerWindowPolicy
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Runs with actual Android window constraints. CI executes this same
 * test suite with compact, 800dp portrait and 1280dp landscape displays.
 */
class PlannerResponsiveWindowTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appRoutesToCorrectLayoutForTheCurrentWindow() {
        val widthDp = rule.activity.resources.configuration.screenWidthDp.toFloat()
        assertTrue(widthDp > 200f)
        val mode = PlannerWindowPolicy.modeFor(widthDp)
        val expected = if (mode == PlannerWindowMode.TWO_PANE) "planner-two-pane"
            else "planner-compact"
        rule.onNodeWithTag(expected).assertExists()
        rule.onNodeWithText("Müfredat Hiyerarşisi").assertDoesNotExist()
        if (mode == PlannerWindowMode.TWO_PANE) {
            rule.onNodeWithTag("sidebar-week-card").assertExists()
        }
        rule.onNodeWithTag("planner-view-toggle").assertExists()
        rule.onNodeWithTag("planner-view-toggle").performClick()
        rule.onNodeWithText("Haftalık").assertExists()
        rule.onNodeWithText("Aylık").assertExists()
        rule.onNodeWithText("Temalar").assertExists()
        rule.onNodeWithText("İlerleme").assertExists()
        rule.onNodeWithText("Rehber & SSS").assertDoesNotExist()
        rule.onNodeWithText("Karşılaştırma").assertDoesNotExist()
        // The note card must be reachable above system navigation bars.
        rule.onNodeWithText("Öğretmen notları ve pedagojik açıklamalar")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
