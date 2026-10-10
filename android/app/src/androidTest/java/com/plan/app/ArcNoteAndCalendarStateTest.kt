package com.plan.app

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.plan.app.core.design.components.core.ArcCalendar
import com.plan.app.core.design.components.extended.ArcRichTextEditor
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.PlanWeek
import org.junit.Rule
import org.junit.Test

class ArcNoteAndCalendarStateTest {
    @get:Rule val rule = createComposeRule()

    @Test fun formattingControlsModifyTextInsteadOfInsertingExampleWords() {
        var note by mutableStateOf("Ders notu")
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcRichTextEditor(value = note, onValueChange = { note = it })
            }
        }
        rule.onNodeWithContentDescription("Kalın").performClick()
        rule.onNodeWithTag("teacher-note-input").assertTextEquals("Ders notu****")
        rule.onNodeWithText("0 karakter").assertDoesNotExist()
    }

    @Test fun browsedMonthSurvivesSavedInstanceStateRestoration() {
        val weeks = listOf(
            PlanWeek(
                weekNumber = 1, month = "EYLÜL", dateRangeRaw = "14-18 Eylül",
                startDate = "2026-09-14", endDate = "2026-09-18", totalHours = 5
            ),
            PlanWeek(
                weekNumber = 2, month = "KASIM", dateRangeRaw = "2-6 Kasım",
                startDate = "2026-11-02", endDate = "2026-11-06", totalHours = 5
            )
        )
        val restoration = StateRestorationTester(rule)
        restoration.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcCalendar(
                    selectedWeek = 1, onSelectWeek = {},
                    planWeeks = weeks, academicBreaks = emptyList()
                )
            }
        }
        rule.onNodeWithContentDescription("Sonraki ay").performClick()
        rule.onNodeWithText("Ekim 2026").assertExists()
        restoration.emulateSavedInstanceStateRestore()
        rule.onNodeWithText("Ekim 2026").assertExists()
    }
}
