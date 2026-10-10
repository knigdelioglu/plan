package com.plan.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.platform.testTag
import com.plan.app.core.design.components.core.ArcCheckbox
import com.plan.app.core.design.components.core.ArcExpandableCard
import com.plan.app.core.design.components.core.ArcSegmentedControl
import com.plan.app.core.design.components.advanced.ArcActivityHeatmap
import com.plan.app.core.design.components.advanced.ArcSwipeActions
import com.plan.app.core.design.components.core.ArcCalendar
import com.plan.app.core.model.PlanWeek
import com.plan.app.core.model.AcademicCalendarBreak
import com.plan.app.core.design.theme.ArcTheme
import org.junit.Assert.assertEquals
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
    @Test
    fun calendarAnnouncesRealTeachingWeekAndAcademicBreak() {
        val week = PlanWeek(
            weekNumber = 1, month = "EYLÜL",
            dateRangeRaw = "14–18 Eylül 2026", startDate = "2026-09-14",
            endDate = "2026-09-18", totalHours = 5
        )
        val vacation = AcademicCalendarBreak(
            breakName = "Ara tatil",
            startDate = "2026-09-21", endDate = "2026-09-25"
        )
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcCalendar(
                    selectedWeek = 1,
                    onSelectWeek = {},
                    planWeeks = listOf(week),
                    academicBreaks = listOf(vacation)
                )
            }
        }
        rule.onNodeWithContentDescription("14 Eylül 2026, 1. öğretim haftası, seçili")
            .assertExists()
        rule.onNodeWithContentDescription("21 Eylül 2026, ara tatil, Ara tatil")
            .assertExists()
    }

    @Test
    fun swipeExposesTwoIndependentAccessibleGestures() {
        var completed by mutableStateOf(false)
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                ArcSwipeActions(
                    onSwipeLeft = { completed = true },
                    onSwipeRight = { completed = false },
                    modifier = Modifier.width(330.dp).height(128.dp).testTag("swipe-progress"),
                    onSwipeLeftLabel = "İşlendi",
                    onSwipeRightLabel = "İşaretini kaldır"
                ) {
                    Box(modifier = Modifier.width(330.dp).height(128.dp)) {
                        Text(if (completed) "Tamamlandı" else "Tamamlanmadı")
                    }
                }
            }
        }
        val actions = rule.onNodeWithTag("swipe-progress")
            .fetchSemanticsNode().config[SemanticsProperties.CustomActions]
        assertEquals(listOf("İşlendi", "İşaretini kaldır"), actions.map { it.label })
        rule.onNodeWithTag("swipe-progress").performTouchInput { swipeLeft() }
        rule.onNodeWithText("Tamamlandı").assertExists()
        rule.onNodeWithTag("swipe-progress").performTouchInput { swipeLeft() }
        rule.onNodeWithText("Tamamlandı").assertExists()
        rule.onNodeWithTag("swipe-progress").performTouchInput { swipeRight() }
        rule.onNodeWithText("Tamamlanmadı").assertExists()
    }

}
