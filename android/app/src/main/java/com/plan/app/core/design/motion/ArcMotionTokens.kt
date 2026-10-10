package com.plan.app.core.design.motion

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.compositionLocalOf

/**
 * Native Jetpack Compose implementation of Arc Motion Tokens.
 * Based on Arc Library motion presets (MIT License, Copyright (c) 2026 Elia Kuratli).
 */
object ArcMotionTokens {
    object Duration {
        const val Instant = 120
        const val Fast = 160
        const val Exit = 180
        const val Standard = 240
        const val Considered = 480
        const val Spring = 580
    }

    object Easing {
        val Enter = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)
        val Exit = CubicBezierEasing(0.7f, 0.0f, 0.84f, 0.0f)
        val Standard = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)
        val InOut = CubicBezierEasing(0.65f, 0.0f, 0.35f, 1.0f)
    }

    object Springs {
        /** Presses, toggles, thumbs, and small indicators. Settles fast with a hint of life. */
        val Snappy = spring<Float>(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium)
        
        /** Panels, height changes, and layout shifts. Critically damped, never overshoots. */
        val Smooth = spring<Float>(dampingRatio = 1.0f, stiffness = Spring.StiffnessLow)
        
        /** Shape morphs, shared layout highlights, and width changes. */
        val Morph = spring<Float>(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
        
        /** Fast responsive feedback */
        val Responsive = spring<Float>(dampingRatio = 0.84f, stiffness = 520f)
        
        /** Gentle settle */
        val Gentle = spring<Float>(dampingRatio = 0.92f, stiffness = 340f)

        fun <T> snappy(): SpringSpec<T> = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium)
        fun <T> smooth(): SpringSpec<T> = spring(dampingRatio = 1.0f, stiffness = Spring.StiffnessLow)
        fun <T> morph(): SpringSpec<T> = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
    }

    object Stagger {
        const val CharMillis = 16L
        const val WordMillis = 40L
        const val LineMillis = 80L
        const val ItemMillis = 35L
    }

    fun <T> standardTween(durationMillis: Int = Duration.Standard): AnimationSpec<T> =
        tween(durationMillis = durationMillis, easing = Easing.Standard)

    fun <T> enterTween(durationMillis: Int = Duration.Standard): AnimationSpec<T> =
        tween(durationMillis = durationMillis, easing = Easing.Enter)

    fun <T> exitTween(durationMillis: Int = Duration.Exit): AnimationSpec<T> =
        tween(durationMillis = durationMillis, easing = Easing.Exit)

    /**
     * Do not animate when the Android user disabled animator animations.
     * Keep a typed specification so Float, Dp and layout transitions agree.
     */
    fun <T> respectMotionPreference(
        reduced: Boolean,
        normal: AnimationSpec<T>
    ): AnimationSpec<T> = if (reduced) snap() else normal
}

val LocalReducedMotion = compositionLocalOf { false }
