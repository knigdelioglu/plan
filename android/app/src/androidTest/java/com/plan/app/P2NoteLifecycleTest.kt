package com.plan.app

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import com.plan.app.core.data.TeacherRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Real Activity stop, not only a simulated buffer unit test. */
class P2NoteLifecycleTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fastTypingIsDurableWhenAppImmediatelyGoesToBackground() {
        val marker = "Yaşam döngüsü kayıt testi"
        rule.waitUntil(timeoutMillis = 20_000) {
            rule.onAllNodesWithText("Öğretmen notları ve pedagojik açıklamalar")
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("Öğretmen notları ve pedagojik açıklamalar")
            .performScrollTo().performClick()
        rule.onNodeWithTag("teacher-note-input").performTextInput(marker)

        val appContext = rule.activity.applicationContext
        // Move to stopped before the 650ms idle writer is required to fire.
        // MainActivity.onPause/onStop must synchronously finish the atomic save.
        rule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)

        val repository = TeacherRepository(appContext)
        val keyClass = repository.activeClassroomId.value
        val keyWeek = repository.activeWeekNumber.value
        val saved = repository.getNote(keyClass, keyWeek, null)?.text.orEmpty()
        assertTrue("onStop must persist the entire draft", saved.contains(marker))

        rule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
    }
}
