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
import com.knigdelioglu.arc.compose.controls.ArcActionButton as DesignSystemActionButton
import com.knigdelioglu.arc.compose.controls.ArcButton as DesignSystemButton
import com.knigdelioglu.arc.compose.controls.ArcButtonSize as DesignSystemButtonSize
import com.knigdelioglu.arc.compose.controls.ArcButtonVariant as DesignSystemButtonVariant

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
    DesignSystemButton(
        onClick = onClick,
        modifier = modifier.height(size.height),
        variant = variant.toDesignSystemVariant(),
        size = size.toDesignSystemSize(),
        enabled = enabled,
        loading = loading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        content = content
    )
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
    DesignSystemActionButton(
        onClick = onClick,
        modifier = modifier.size(size),
        variant = variant.toDesignSystemVariant(),
        size = DesignSystemButtonSize.Md,
        enabled = enabled,
        shape = shape,
        content = content
    )
}

private fun ArcButtonVariant.toDesignSystemVariant(): DesignSystemButtonVariant = when (this) {
    ArcButtonVariant.PRIMARY -> DesignSystemButtonVariant.Primary
    ArcButtonVariant.SECONDARY -> DesignSystemButtonVariant.Secondary
    ArcButtonVariant.SUBTLE -> DesignSystemButtonVariant.Ghost
    ArcButtonVariant.OUTLINE -> DesignSystemButtonVariant.Outline
    ArcButtonVariant.DANGER -> DesignSystemButtonVariant.Danger
}

private fun ArcButtonSize.toDesignSystemSize(): DesignSystemButtonSize = when (this) {
    ArcButtonSize.SM -> DesignSystemButtonSize.Sm
    ArcButtonSize.MD -> DesignSystemButtonSize.Md
    ArcButtonSize.LG -> DesignSystemButtonSize.Lg
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
