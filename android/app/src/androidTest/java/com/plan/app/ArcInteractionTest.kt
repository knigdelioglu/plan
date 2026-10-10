package com.plan.app

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.platform.testTag
import com.plan.app.core.design.components.core.ArcCheckbox
import com.plan.app.core.design.components.core.ArcExpandableCard
import com.plan.app.core.design.components.core.ArcSegmentedControl
import com.plan.app.core.design.components.advanced.ArcActivityHeatmap
import com.plan.app.core.design.theme.ArcTheme
import org.junit.Rule
import org.junit.Test

class ArcInteractionTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun accessibleFourTabSelectionWorksWithMotionDisabled() {
        var selected by mutableStateOf("Haftalık")
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcSegmentedControl(
                    options = listOf("Haftalık", "Aylık", "Temalar", "İlerleme"),
                    selected = selected,
                    onSelect = { selected = it },
                    title = { it }
                )
            }
        }
        rule.onNodeWithText("Aylık").performClick()
        rule.onNodeWithText("Aylık").assertIsSelected()
        rule.onNodeWithText("Haftalık").performClick()
        rule.onNodeWithText("Haftalık").assertIsSelected()
    }

    @Test
    fun completionCheckboxExposesToggleSemantics() {
        var completed by mutableStateOf(false)
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcCheckbox(
                    checked = completed,
                    onCheckedChange = { completed = it },
                    modifier = Modifier.testTag("lesson-completed")
                )
            }
        }
        rule.onNodeWithTag("lesson-completed").assertIsOff()
        rule.onNodeWithTag("lesson-completed").performClick()
        rule.onNodeWithTag("lesson-completed").assertIsOn()
    }

    @Test
    fun expandableLessonDetailsRemainAvailableWithoutAnimation() {
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcExpandableCard(title = "Ders ayrıntıları") {
                    Text("Öğrenme çıktısı ve süreç bileşenleri")
                }
            }
        }
        rule.onNodeWithText("Öğrenme çıktısı ve süreç bileşenleri").assertDoesNotExist()
        rule.onNodeWithText("Ders ayrıntıları").performClick()
        rule.onNodeWithText("Öğrenme çıktısı ve süreç bileşenleri").assertIsDisplayed()
    }

    @Test
    fun actualProgressWeeksHaveAccessibleActions() {
        var selectedWeek by mutableStateOf(1)
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcActivityHeatmap(
                    totalWeeks = 9,
                    completedWeeks = setOf(2),
                    currentWeek = selectedWeek,
                    onSelectWeek = { selectedWeek = it }
                )
            }
        }
        rule.onNodeWithContentDescription("2. hafta, tamamlandı").assertIsDisplayed()
        rule.onNodeWithContentDescription("3. hafta, tamamlanmadı").performClick()
        rule.onNodeWithContentDescription("3. hafta, seçili").assertIsDisplayed()
    }
}
