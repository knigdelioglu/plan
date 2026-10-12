package com.plan.app

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.core.view.WindowCompat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verify both themes synchronize Android's status/navigation icon contrast. */
class SystemBarsThemeTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun themeSwitchUpdatesStatusAndNavigationIconAppearance() {
        var initialLightIconsFlag = false
        rule.runOnIdle {
            val activity = rule.activity
            val bars = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            initialLightIconsFlag = bars.isAppearanceLightStatusBars
            assertEquals(initialLightIconsFlag, bars.isAppearanceLightNavigationBars)
        }

        // The theme preference now lives in the dedicated Settings screen.
        rule.onNodeWithContentDescription("Ayarlar").performClick()
        val originalLabel = if (initialLightIconsFlag) "Açık Tema" else "Koyu Tema"
        val toggledLabel = if (initialLightIconsFlag) "Koyu Tema" else "Açık Tema"
        rule.onNodeWithContentDescription(originalLabel).performClick()
        rule.waitForIdle()

        rule.runOnIdle {
            val activity = rule.activity
            val bars = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            assertEquals(!initialLightIconsFlag, bars.isAppearanceLightStatusBars)
            assertEquals(!initialLightIconsFlag, bars.isAppearanceLightNavigationBars)
        }

        // Restore the user's test fixture preference.
        rule.onNodeWithContentDescription(toggledLabel).performClick()
    }
}
