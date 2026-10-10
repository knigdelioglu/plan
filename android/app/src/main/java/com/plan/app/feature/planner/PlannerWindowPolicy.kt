package com.plan.app.feature.planner

/** Window *dp*, never raw pixels: supports tablets, DeX and resizeable windows. */
enum class PlannerWindowMode { COMPACT, TWO_PANE }

object PlannerWindowPolicy {
    const val TwoPaneThresholdDp = 950f
    const val MinSidebarDp = 390f
    const val MinWorkspaceDp = 480f
    const val DividerDp = 24f

    fun modeFor(widthDp: Float): PlannerWindowMode =
        if (widthDp >= TwoPaneThresholdDp) PlannerWindowMode.TWO_PANE
        else PlannerWindowMode.COMPACT

    fun clampSidebarFraction(
        widthDp: Float,
        desiredFraction: Float,
        minSidebarDp: Float = MinSidebarDp,
        minWorkspaceDp: Float = MinWorkspaceDp,
        dividerDp: Float = DividerDp
    ): Float {
        val usable = (widthDp - dividerDp).coerceAtLeast(1f)
        val minLeft = minSidebarDp.coerceIn(0f, usable)
        val maxLeft = (usable - minWorkspaceDp).coerceIn(minLeft, usable)
        return (usable * desiredFraction).coerceIn(minLeft, maxLeft) / usable
    }
}
