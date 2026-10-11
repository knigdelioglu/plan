package com.plan.app

import android.os.Bundle
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
        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            DesignSystemTheme(darkTheme = isDark, accent = ArcAccent.Neutral) {
                ArcTheme(darkTheme = isDark) {
                    PlannerScreen(viewModel = viewModel)
                }
            }
        }
    }
}
