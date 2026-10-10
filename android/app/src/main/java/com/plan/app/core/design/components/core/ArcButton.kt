package com.plan.app.core.design.components.core

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.motion.LocalReducedMotion
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

enum class ArcButtonVariant {
    PRIMARY,
    SECONDARY,
    SUBTLE,
    OUTLINE,
    DANGER
}

enum class ArcButtonSize(val height: Dp, val horizontalPadding: Dp, val fontSize: Int) {
    SM(48.dp, 12.dp, 13),
    MD(48.dp, 16.dp, 14),
    LG(50.dp, 20.dp, 15)
}

/**
 * Arc Button Component (1 / 70)
 */
@Composable
fun ArcButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ArcButtonVariant = ArcButtonVariant.PRIMARY,
    size: ArcButtonSize = ArcButtonSize.MD,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = ArcRadius.control,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = ArcTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1.0f,
        animationSpec = ArcMotionTokens.respectMotionPreference(
            reducedMotion, ArcMotionTokens.Springs.Snappy
        ),
        label = "button_scale"
    )

    val (bgColor, contentColor, borderStroke) = when (variant) {
        ArcButtonVariant.PRIMARY -> Triple(
            if (enabled) colors.accent else colors.surfaceMuted,
            if (enabled) colors.accentForeground else colors.textMuted,
            null
        )
        ArcButtonVariant.SECONDARY -> Triple(
            if (enabled) colors.surfaceMuted else colors.surfaceMuted.copy(alpha = 0.5f),
            if (enabled) colors.foreground else colors.textMuted,
            BorderStroke(1.dp, colors.border)
        )
        ArcButtonVariant.SUBTLE -> Triple(
            if (enabled) Color.Transparent else Color.Transparent,
            if (enabled) colors.foreground else colors.textMuted,
            null
        )
        ArcButtonVariant.OUTLINE -> Triple(
            Color.Transparent,
            if (enabled) colors.foreground else colors.textMuted,
            BorderStroke(1.dp, if (enabled) colors.borderStrong else colors.border)
        )
        ArcButtonVariant.DANGER -> Triple(
            if (enabled) colors.danger else colors.surfaceMuted,
            if (enabled) Color.White else colors.textMuted,
            null
        )
    }

    Surface(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .scale(scale)
            .height(size.height),
        shape = shape,
        color = bgColor,
        contentColor = contentColor,
        border = borderStroke,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = size.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                leadingIcon?.invoke()
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content()
                }
                trailingIcon?.invoke()
            }
        }
    }
}

/**
 * Arc Action Button Component (2 / 70) - Square or circular compact icon button
 */
@Composable
fun ArcActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = ArcRadius.control,
    enabled: Boolean = true,
    variant: ArcButtonVariant = ArcButtonVariant.SECONDARY,
    content: @Composable () -> Unit
) {
    val colors = ArcTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = LocalReducedMotion.current
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1.0f,
        animationSpec = ArcMotionTokens.respectMotionPreference(
            reducedMotion, ArcMotionTokens.Springs.Snappy
        ),
        label = "action_button_scale"
    )

    val (bgColor, contentColor, borderStroke) = when (variant) {
        ArcButtonVariant.PRIMARY -> Triple(colors.accent, colors.accentForeground, null)
        ArcButtonVariant.SECONDARY -> Triple(colors.surfaceMuted, colors.foreground, BorderStroke(1.dp, colors.border))
        ArcButtonVariant.SUBTLE -> Triple(Color.Transparent, colors.foreground, null)
        ArcButtonVariant.OUTLINE -> Triple(Color.Transparent, colors.foreground, BorderStroke(1.dp, colors.border))
        ArcButtonVariant.DANGER -> Triple(colors.dangerSubtle, colors.danger, null)
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .size(size),
        shape = shape,
        color = bgColor,
        contentColor = contentColor,
        border = borderStroke,
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * Arc Button Group Component (3 / 70)
 */
@Composable
fun ArcButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = ArcRadius.control,
        color = ArcTheme.colors.surfaceMuted,
        border = BorderStroke(1.dp, ArcTheme.colors.border)
    ) {
        Row(
            modifier = Modifier.padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            content()
        }
    }
}
