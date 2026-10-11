package com.plan.app.core.design.components.core

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.knigdelioglu.arc.compose.display.ArcBadge as DesignSystemBadge
import com.knigdelioglu.arc.compose.display.ArcBadgeVariant as DesignSystemBadgeVariant

enum class ArcBadgeVariant {
    NEUTRAL,
    ACCENT,
    SUCCESS,
    WARNING,
    DANGER,
    OUTLINE
}

/**
 * Arc Badge Component (13 / 70)
 */
@Composable
fun ArcBadge(
    text: String,
    modifier: Modifier = Modifier,
    variant: ArcBadgeVariant = ArcBadgeVariant.NEUTRAL,
    leadingDot: Boolean = false
) {
    if (!leadingDot && variant != ArcBadgeVariant.OUTLINE) {
        DesignSystemBadge(
            text = text,
            modifier = modifier,
            variant = when (variant) {
                ArcBadgeVariant.NEUTRAL -> DesignSystemBadgeVariant.Default
                ArcBadgeVariant.ACCENT -> DesignSystemBadgeVariant.Accent
                ArcBadgeVariant.SUCCESS -> DesignSystemBadgeVariant.Success
                ArcBadgeVariant.WARNING -> DesignSystemBadgeVariant.Warning
                ArcBadgeVariant.DANGER -> DesignSystemBadgeVariant.Danger
                ArcBadgeVariant.OUTLINE -> DesignSystemBadgeVariant.Default
            }
        )
        return
    }

    val colors = ArcTheme.colors
    val (bgColor, textColor, borderColor) = when (variant) {
        ArcBadgeVariant.NEUTRAL -> Triple(colors.surfaceMuted, colors.textSecondary, colors.border)
        ArcBadgeVariant.ACCENT -> Triple(colors.accentSubtle, colors.accent, colors.border)
        ArcBadgeVariant.SUCCESS -> Triple(colors.successSubtle, colors.success, colors.success.copy(alpha = 0.3f))
        ArcBadgeVariant.WARNING -> Triple(colors.warningSubtle, colors.warning, colors.warning.copy(alpha = 0.3f))
        ArcBadgeVariant.DANGER -> Triple(colors.dangerSubtle, colors.danger, colors.danger.copy(alpha = 0.3f))
        ArcBadgeVariant.OUTLINE -> Triple(Color.Transparent, colors.foreground, colors.borderStrong)
    }

    Surface(
        modifier = modifier,
        shape = ArcRadius.pill,
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (leadingDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(textColor)
                )
            }
            Text(
                text = text,
                style = ArcTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Arc Tooltip Component (14 / 70)
 */
@Composable
fun ArcTooltip(
    tooltipText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(modifier = Modifier.clickable { showTooltip = !showTooltip }) {
            content()
        }

        if (showTooltip) {
            Popup(onDismissRequest = { showTooltip = false }) {
                Surface(
                    shape = ArcRadius.sm,
                    color = ArcTheme.colors.accent,
                    shadowElevation = 6.dp,
                    modifier = Modifier.padding(ArcSpacing.space2)
                ) {
                    Text(
                        text = tooltipText,
                        style = ArcTheme.typography.bodySmall,
                        color = ArcTheme.colors.accentForeground,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Arc Popover Component (15 / 70)
 */
@Composable
fun ArcPopover(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    anchorContent: @Composable () -> Unit,
    popoverContent: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        anchorContent()

        if (isOpen) {
            Popup(onDismissRequest = onDismiss) {
                Surface(
                    shape = ArcRadius.md,
                    color = ArcTheme.colors.surface,
                    border = BorderStroke(1.dp, ArcTheme.colors.border),
                    shadowElevation = 12.dp,
                    modifier = Modifier.padding(ArcSpacing.space2)
                ) {
                    Box(modifier = Modifier.padding(ArcSpacing.space3)) {
                        popoverContent()
                    }
                }
            }
        }
    }
}

/**
 * Arc Progress Component (16 / 70)
 */
@Composable
fun ArcProgress(
    progress: Float, // 0f to 1f
    modifier: Modifier = Modifier,
    color: Color = ArcTheme.colors.accent,
    trackColor: Color = ArcTheme.colors.surfaceMuted,
    strokeWidth: Dp = 6.dp
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth)
            .clip(ArcRadius.pill),
        color = color,
        trackColor = trackColor
    )
}

/**
 * Arc Skeleton Component (17 / 70) - Shimmer loading box
 */
@Composable
fun ArcSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = ArcRadius.sm
) {
    val shimmerColors = listOf(
        ArcTheme.colors.surfaceMuted.copy(alpha = 0.6f),
        ArcTheme.colors.surfaceMuted.copy(alpha = 0.2f),
        ArcTheme.colors.surfaceMuted.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "skeleton_transition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * Arc Empty State Component (18 / 70)
 */
@Composable
fun ArcEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(ArcSpacing.space8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(ArcTheme.colors.surfaceMuted),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.height(ArcSpacing.space4))
        }

        Text(
            text = title,
            style = ArcTheme.typography.titleMedium,
            color = ArcTheme.colors.foreground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ArcSpacing.space2))

        Text(
            text = description,
            style = ArcTheme.typography.bodyMedium,
            color = ArcTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        if (action != null) {
            Spacer(modifier = Modifier.height(ArcSpacing.space5))
            action()
        }
    }
}
