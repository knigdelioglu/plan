package com.plan.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.LearningOutcome
import com.plan.app.feature.planner.LessonLearningOutcomes
import org.junit.Rule
import org.junit.Test

class LessonLearningOutcomesTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun completeLearningOutcomesAppearWithoutAnyTap() {
        rule.setContent {
            ArcTheme(reducedMotionOverride = true) {
                LessonLearningOutcomes(
                    outcomes = listOf(
                        LearningOutcome("TDE1.1", "Dinlemeyi/izlemeyi yönetebilme"),
                        LearningOutcome("TDE1.2", "Dinlenen/izlenen metinlerde anlam oluşturabilme")
                    )
                )
            }
        }

        rule.onNodeWithText("Öğrenme Çıktıları").assertIsDisplayed()
        rule.onNodeWithText("TDE1.1").assertIsDisplayed()
        rule.onNodeWithText("Dinlemeyi/izlemeyi yönetebilme").assertIsDisplayed()
        rule.onNodeWithText("TDE1.2").assertIsDisplayed()
        rule.onNodeWithText("Dinlenen/izlenen metinlerde anlam oluşturabilme").assertIsDisplayed()
    }
}
