package com.plan.app

import androidx.compose.animation.core.SnapSpec
import com.plan.app.core.design.motion.AndroidMotionPreference
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.feature.planner.PlannerWindowMode
import com.plan.app.feature.planner.PlannerWindowPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class P2MotionAndWindowPolicyTest {
    @Test
    fun androidAnimationOffUsesInstantArcTransitions() {
        assertTrue(AndroidMotionPreference.shouldReduce(0f))
        assertTrue(AndroidMotionPreference.shouldReduce(-1f))
        assertTrue(AndroidMotionPreference.shouldReduce(Float.NaN))
        assertFalse(AndroidMotionPreference.shouldReduce(0.5f))
        assertFalse(AndroidMotionPreference.shouldReduce(1f))
        val normal = ArcMotionTokens.Springs.Snappy
        assertTrue(ArcMotionTokens.respectMotionPreference(true, normal) is SnapSpec<*>)
        assertEquals(normal, ArcMotionTokens.respectMotionPreference(false, normal))
    }

    @Test
    fun responsiveBreakpointsKeepSmallTabletsAndDexWindowsUsable() {
        listOf(360f, 600f, 840f, 949f).forEach {
            assertEquals(PlannerWindowMode.COMPACT, PlannerWindowPolicy.modeFor(it))
        }
        listOf(950f, 1200f, 1920f).forEach {
            assertEquals(PlannerWindowMode.TWO_PANE, PlannerWindowPolicy.modeFor(it))
        }
    }

    @Test
    fun resizableSidebarRespectsBothMinimumWidths() {
        val width = 950f
        val usable = width - PlannerWindowPolicy.DividerDp
        val minRatio = PlannerWindowPolicy.MinSidebarDp / usable
        val maxRatio = (usable - PlannerWindowPolicy.MinWorkspaceDp) / usable
        assertEquals(minRatio, PlannerWindowPolicy.clampSidebarFraction(width, 0f), 0.0001f)
        assertEquals(maxRatio, PlannerWindowPolicy.clampSidebarFraction(width, 1f), 0.0001f)
        val mid = PlannerWindowPolicy.clampSidebarFraction(1600f, 0.35f)
        assertTrue(mid >= 0f && mid <= 1f)
        assertEquals(0.35f, mid, 0.0001f)
    }
}
