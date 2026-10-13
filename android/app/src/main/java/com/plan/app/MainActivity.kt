package com.plan.app

import android.os.Bundle
import android.os.Build
import android.graphics.Color as AndroidColor
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.knigdelioglu.arc.compose.foundation.ArcAccent
import com.knigdelioglu.arc.compose.foundation.ArcTheme as DesignSystemTheme
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.feature.planner.PlannerScreen
import com.plan.app.feature.planner.PlannerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PlannerViewModel by viewModels()

    // Flush while the Activity is still alive, before Android may reclaim it.
    override fun onResume() {
        super.onResume()
        viewModel.onAppResumed()
    }

    override fun onPause() {
        viewModel.flushPendingTeacherNotes()
        super.onPause()
    }

    override fun onStop() {
        viewModel.flushPendingTeacherNotes()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Draw beneath Android's system bars; Compose handles safe drawing insets.
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Avoid Android's automatic light scrim over three-button navigation.
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            val fontScalePercent by viewModel.fontScalePercent.collectAsState()
            val systemDensity = LocalDensity.current
            // The app's preference is independent of the device-wide night mode.
            // Re-apply platform icon appearance on every Compose theme change.
            SideEffect { syncSystemBars(isDark) }
            // Keep the device's accessibility font scaling and scale sp, not dp.
            CompositionLocalProvider(
                LocalDensity provides Density(
                    systemDensity.density,
                    systemDensity.fontScale * (fontScalePercent / 100f)
                )
            ) {
                DesignSystemTheme(darkTheme = isDark, accent = ArcAccent.Neutral) {
                    ArcTheme(darkTheme = isDark) {
                        PlannerScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
    @Suppress("DEPRECATION")
    private fun syncSystemBars(dark: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        // "light" means DARK icons on a LIGHT background.
        controller.isAppearanceLightStatusBars = !dark
        controller.isAppearanceLightNavigationBars = !dark

        // Keep the platform decor flags in sync on Android versions that may
        // reapply legacy light-icon flags after edge-to-edge configuration.
        val decor = window.decorView
        val mask = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        decor.systemUiVisibility =
            if (dark) decor.systemUiVisibility and mask.inv() else decor.systemUiVisibility or mask

        // Before Android 10, the navigation bar cannot rely on transparent
        // edge-to-edge rendering on all navigation modes.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            window.navigationBarColor = if (dark) AndroidColor.rgb(9, 9, 11) else AndroidColor.WHITE
        }
    }

}
