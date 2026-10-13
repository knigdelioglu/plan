package com.plan.app.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import com.knigdelioglu.arc.compose.foundation.ArcTheme as SharedArcTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.plan.app.core.design.motion.LocalReducedMotion
import com.plan.app.core.design.motion.rememberSystemReducedMotion
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Arc Design System Colors based on Arc foundation.css (OKLCH translated to native Compose Color).
 * MIT License, Copyright (c) 2026 Elia Kuratli.
 */
data class ArcColorPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val surfaceMuted: Color,
    val foreground: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val borderSubtle: Color,
    val borderStrong: Color,
    val accent: Color,
    val accentStrong: Color,
    val accentSubtle: Color,
    val accentForeground: Color,
    val controlOn: Color,
    val controlGlyph: Color,
    val controlTrack: Color,
    val controlTrackHover: Color,
    val controlThumb: Color,
    val controlThumbOn: Color,
    val success: Color,
    val successSubtle: Color,
    val warning: Color,
    val warningSubtle: Color,
    val danger: Color,
    val dangerSubtle: Color,
    val arcGradientFrom: Color,
    val arcGradientTo: Color,
    val arcGradientForeground: Color,
    val neutral0: Color,
    val neutral1: Color,
    val neutral2: Color,
    val neutral3: Color,
    val neutral4: Color,
    val neutral5: Color,
    val neutral6: Color,
    val neutral7: Color,
    val neutral8: Color,
    val neutral9: Color,
    val neutral10: Color,
    val neutral11: Color
) {
    val brandGradient: Brush
        get() = Brush.linearGradient(listOf(arcGradientFrom, arcGradientTo))

    val softGradient: Brush
        get() = if (isDark) {
            Brush.linearGradient(listOf(Color(0xFF232029), Color(0xFF262222)))
        } else {
            Brush.linearGradient(listOf(Color(0xFFF9F7FD), Color(0xFFFDF9F7)))
        }
}

val ArcLightColors = ArcColorPalette(
    isDark = false,
    background = Color(0xFFFCFCFC),
    surface = Color(0xFFFFFFFF),
    surfaceRaised = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFF5F5F7),
    foreground = Color(0xFF18181B),
    textSecondary = Color(0xFF71717A),
    textMuted = Color(0xFFA1A1AA),
    border = Color(0xFFE4E4E7),
    borderSubtle = Color(0xFFF0F0F2),
    borderStrong = Color(0xFFD4D4D8),
    accent = Color(0xFF18181B),
    accentStrong = Color(0xFF09090B),
    accentSubtle = Color(0x1A18181B),
    accentForeground = Color(0xFFFFFFFF),
    controlOn = Color(0xFF18181B),
    controlGlyph = Color(0xFFFFFFFF),
    controlTrack = Color(0xFFE4E4E7),
    controlTrackHover = Color(0xFFD4D4D8),
    controlThumb = Color(0xFFFFFFFF),
    controlThumbOn = Color(0xFFFFFFFF),
    success = Color(0xFF16A34A),
    successSubtle = Color(0x1A16A34A),
    warning = Color(0xFFD97706),
    warningSubtle = Color(0x1AD97706),
    danger = Color(0xFFDC2626),
    dangerSubtle = Color(0x1ADC2626),
    arcGradientFrom = Color(0xFFE5DEFF),
    arcGradientTo = Color(0xFFFFE7D6),
    arcGradientForeground = Color(0xFF2E1065),
    neutral0 = Color(0xFFFFFFFF),
    neutral1 = Color(0xFFFAFAFA),
    neutral2 = Color(0xFFF4F4F5),
    neutral3 = Color(0xFFE4E4E7),
    neutral4 = Color(0xFFD4D4D8),
    neutral5 = Color(0xFFA1A1AA),
    neutral6 = Color(0xFF71717A),
    neutral7 = Color(0xFF52525B),
    neutral8 = Color(0xFF3F3F46),
    neutral9 = Color(0xFF27272A),
    neutral10 = Color(0xFF18181B),
    neutral11 = Color(0xFF09090B)
)

val ArcDarkColors = ArcColorPalette(
    isDark = true,
    background = Color(0xFF09090B),
    surface = Color(0xFF121215),
    surfaceRaised = Color(0xFF18181C),
    surfaceMuted = Color(0xFF202026),
    foreground = Color(0xFFF4F4F5),
    textSecondary = Color(0xFFA1A1AA),
    textMuted = Color(0xFF71717A),
    border = Color(0xFF27272A),
    borderSubtle = Color(0xFF1C1C20),
    borderStrong = Color(0xFF3F3F46),
    accent = Color(0xFFFAFAFA),
    accentStrong = Color(0xFFFFFFFF),
    accentSubtle = Color(0x26FAFAFA),
    accentForeground = Color(0xFF09090B),
    controlOn = Color(0xFFFAFAFA),
    controlGlyph = Color(0xFF09090B),
    controlTrack = Color(0xFF27272A),
    controlTrackHover = Color(0xFF3F3F46),
    controlThumb = Color(0xFFFAFAFA),
    controlThumbOn = Color(0xFF09090B),
    success = Color(0xFF22C55E),
    successSubtle = Color(0x2622C55E),
    warning = Color(0xFFF59E0B),
    warningSubtle = Color(0x26F59E0B),
    danger = Color(0xFFEF4444),
    dangerSubtle = Color(0x26EF4444),
    arcGradientFrom = Color(0xFF4C1D95),
    arcGradientTo = Color(0xFF7C2D12),
    arcGradientForeground = Color(0xFFF5F3FF),
    neutral0 = Color(0xFF09090B),
    neutral1 = Color(0xFF121215),
    neutral2 = Color(0xFF18181C),
    neutral3 = Color(0xFF202026),
    neutral4 = Color(0xFF27272A),
    neutral5 = Color(0xFF3F3F46),
    neutral6 = Color(0xFF52525B),
    neutral7 = Color(0xFF71717A),
    neutral8 = Color(0xFFA1A1AA),
    neutral9 = Color(0xFFD4D4D8),
    neutral10 = Color(0xFFE4E4E7),
    neutral11 = Color(0xFFFAFAFA)
)

object ArcSpacing {
    val space1: Dp = 4.dp
    val space2: Dp = 8.dp
    val space3: Dp = 12.dp
    val space4: Dp = 16.dp
    val space5: Dp = 20.dp
    val space6: Dp = 24.dp
    val space8: Dp = 32.dp
    val space10: Dp = 40.dp
    val space12: Dp = 48.dp
    val space16: Dp = 64.dp
    val space20: Dp = 80.dp
    val space24: Dp = 96.dp
}

object ArcRadius {
    val none = RoundedCornerShape(0.dp)
    val xs = RoundedCornerShape(4.dp)
    val sm = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val control = RoundedCornerShape(18.dp)
    val panel = RoundedCornerShape(26.dp)
    val surface = RoundedCornerShape(34.dp)
    val pill = RoundedCornerShape(9999.dp)
}

object ArcTypographyTokens {
    val displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.02).sp
    )
    val displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.015).sp
    )
    val titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.01).sp
    )
    val titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
    val bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    val bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
    val labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
}

val LocalArcColors = compositionLocalOf { ArcLightColors }

object ArcTheme {
    val colors: ArcColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalArcColors.current

    val spacing: ArcSpacing = ArcSpacing
    val radius: ArcRadius = ArcRadius
    val typography: ArcTypographyTokens = ArcTypographyTokens
}

/**
 * Compatibility adapter for the application's older Arc API.
 * The shared Compose Design System owns MaterialTheme and canonical color tokens.
 * Existing app-specific components consume the mapped palette while they migrate.
 */
@Composable
fun ArcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reducedMotionOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    val shared = SharedArcTheme.colors
    val fallback = if (darkTheme) ArcDarkColors else ArcLightColors
    val colors = if (shared.isDark == darkTheme) {
        fallback.copy(
            background = shared.background,
            surface = shared.surface,
            surfaceRaised = shared.surfaceRaised,
            surfaceMuted = shared.surfaceMuted,
            foreground = shared.foreground,
            textSecondary = shared.textSecondary,
            textMuted = shared.textMuted,
            border = shared.border,
            borderSubtle = shared.borderSubtle,
            borderStrong = shared.borderStrong,
            accent = shared.accent,
            accentStrong = shared.accentStrong,
            accentSubtle = shared.accentSubtle,
            accentForeground = shared.accentForeground,
            controlOn = shared.controlOn,
            controlGlyph = shared.controlGlyph,
            controlTrack = shared.controlTrack,
            controlThumb = shared.controlThumb,
            success = shared.success,
            warning = shared.warning,
            danger = shared.danger,
            arcGradientFrom = shared.gradientFrom,
            arcGradientTo = shared.gradientTo,
            arcGradientForeground = shared.gradientForeground
        )
    } else fallback
    val reducedMotion = reducedMotionOverride ?: rememberSystemReducedMotion()
    CompositionLocalProvider(
        LocalArcColors provides colors,
        LocalReducedMotion provides reducedMotion
    ) { content() }
}
