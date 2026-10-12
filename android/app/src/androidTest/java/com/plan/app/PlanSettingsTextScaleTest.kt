package com.plan.app

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.plan.app.core.data.TeacherRepository
import com.plan.app.core.model.FontScalePreference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PlanSettingsTextScaleTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun textScaleChangePersistsAndTheSettingsScreenCanClose() {
        val context = rule.activity.applicationContext
        val initial = TeacherRepository(context).fontScalePercent.value
        rule.onNodeWithTag("settings-open-button").performClick()
        rule.onNodeWithTag("plan-settings").assertExists()
        rule.onNodeWithTag("font-scale-preview").assertExists()
        rule.onNodeWithText("%$initial").assertIsDisplayed()
        val increase = initial < FontScalePreference.MAX_PERCENT
        val firstButton = if (increase) "font-scale-increase" else "font-scale-decrease"
        val secondButton = if (increase) "font-scale-decrease" else "font-scale-increase"
        val changed = initial + (if (increase) 5 else -5)
        try {
            rule.onNodeWithTag(firstButton).performClick()
            rule.onNodeWithText("%$changed").assertIsDisplayed()
            assertEquals(changed, TeacherRepository(context).fontScalePercent.value)
        } finally {
            rule.onNodeWithTag(secondButton).performClick()
        }
        rule.onNodeWithText("%$initial").assertIsDisplayed()
        rule.onNodeWithTag("settings-back-button").performClick()
        rule.onNodeWithTag("planner-view-toggle").assertExists()
    }
}
