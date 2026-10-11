package com.plan.app.core.design.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Tabs Component (8 / 70)
 */
enum class ArcTabVariant {
    PILL,
    UNDERLINE
}

@Composable
fun <T> ArcTabs(
    tabs: List<T>,
    selectedTab: T,
    onTabSelected: (T) -> Unit,
    tabTitle: (T) -> String,
    modifier: Modifier = Modifier,
    variant: ArcTabVariant = ArcTabVariant.PILL,
    badgeCount: ((T) -> Int?)? = null
) {
    val colors = ArcTheme.colors
    val scrollState = rememberScrollState()

    if (variant == ArcTabVariant.PILL) {
        Surface(
            modifier = modifier,
            shape = ArcRadius.pill,
            color = colors.surfaceMuted,
            border = BorderStroke(1.dp, colors.border)
        ) {
            Row(
                modifier = Modifier
                    .padding(3.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val isSelected = tab == selectedTab
                    val pillBg = if (isSelected) colors.surface else Color.Transparent
                    val pillText = if (isSelected) colors.foreground else colors.textSecondary
                    val count = badgeCount?.invoke(tab)

                    Box(
                        modifier = Modifier
                            .clip(ArcRadius.pill)
                            .background(pillBg)
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = tabTitle(tab),
                                style = ArcTheme.typography.labelMedium,
                                color = pillText
                            )
                            if (count != null && count > 0) {
                                ArcBadge(
                                    text = count.toString(),
                                    variant = if (isSelected) ArcBadgeVariant.ACCENT else ArcBadgeVariant.NEUTRAL
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tabTitle(tab),
                        style = ArcTheme.typography.titleMedium,
                        color = if (isSelected) colors.foreground else colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(24.dp)
                            .background(if (isSelected) colors.accent else Color.Transparent)
                    )
                }
            }
        }
    }
}

/**
 * Arc Drawer Component (9 / 70)
 */
@Composable
fun ArcDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 320.dp,
    content: @Composable () -> Unit
) {
    if (isOpen) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { onClose() }
            )
            Surface(
                modifier = modifier
                    .width(width)
                    .fillMaxHeight(),
                color = ArcTheme.colors.surface,
                shadowElevation = 8.dp
            ) {
                content()
            }
        }
    }
}

/**
 * Arc Resizable Panels Component (10 / 70)
 * Allows horizontal multi-column resizing on tablets & DeX.
 */
@Composable
fun ArcResizablePanels(
    modifier: Modifier = Modifier,
    initialSplitRatio: Float = 0.35f,
    controlledSplitRatio: Float? = null,
    onSplitRatioChange: ((Float) -> Unit)? = null,
    minFirstPanelWidth: Dp = 260.dp,
    minSecondPanelWidth: Dp = 380.dp,
    firstPanel: @Composable () -> Unit,
    secondPanel: @Composable () -> Unit
) {
    val safeInitialRatio = initialSplitRatio.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0.4f
    var internalSplitRatio by rememberSaveable {
        mutableFloatStateOf(safeInitialRatio)
    }
    val splitRatio = (controlledSplitRatio ?: internalSplitRatio)
        .takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: safeInitialRatio
    fun updateSplitRatio(newRatio: Float) {
        internalSplitRatio = newRatio
        onSplitRatioChange?.invoke(newRatio)
    }
    val density = LocalDensity.current
    val dividerWidth = 24.dp

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val usableWidth = (maxWidth - dividerWidth).coerceAtLeast(1.dp)
        val minimumFirst = minFirstPanelWidth.coerceAtMost(usableWidth)
        val maximumFirst = (usableWidth - minSecondPanelWidth).coerceAtLeast(minimumFirst)
            .coerceAtMost(usableWidth)
        val firstPanelWidth = (usableWidth * splitRatio).coerceIn(minimumFirst, maximumFirst)
        val minimumRatio = minimumFirst.value / usableWidth.value
        val maximumRatio = maximumFirst.value / usableWidth.value

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(firstPanelWidth)
                    .fillMaxHeight()
            ) {
                firstPanel()
            }

            // Wider touch target with visible 2dp divider; keyboard arrows support DeX.
            Box(
                modifier = Modifier
                    .width(dividerWidth)
                    .fillMaxHeight()
                    .semantics {
                        contentDescription = "Panel genişliğini değiştir"
                        stateDescription = "Sol panel ${firstPanelWidth.value.toInt()} dp"
                        progressBarRangeInfo = ProgressBarRangeInfo(
                            splitRatio.coerceIn(minimumRatio, maximumRatio),
                            minimumRatio..maximumRatio,
                            0
                        )
                        customActions = listOf(
                            CustomAccessibilityAction("Sol paneli daralt") {
                                updateSplitRatio((splitRatio - 20f / usableWidth.value).coerceIn(minimumRatio, maximumRatio))
                                true
                            },
                            CustomAccessibilityAction("Sol paneli genişlet") {
                                updateSplitRatio((splitRatio + 20f / usableWidth.value).coerceIn(minimumRatio, maximumRatio))
                                true
                            }
                        )
                    }
                    .onKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                        val step = if (event.isShiftPressed) 64f else 20f
                        when (event.key) {
                            Key.DirectionLeft -> updateSplitRatio(
                                (splitRatio - step / usableWidth.value).coerceIn(minimumRatio, maximumRatio)
                            )
                            Key.DirectionRight -> updateSplitRatio(
                                (splitRatio + step / usableWidth.value).coerceIn(minimumRatio, maximumRatio)
                            )
                            Key.MoveHome -> updateSplitRatio(minimumRatio)
                            Key.MoveEnd -> updateSplitRatio(maximumRatio)
                            else -> return@onKeyEvent false
                        }
                        true
                    }
                    .focusable()
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            val deltaDp = with(density) { delta.toDp().value }
                            updateSplitRatio(
                                (splitRatio + deltaDp / usableWidth.value)
                                    .coerceIn(minimumRatio, maximumRatio)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(ArcTheme.colors.border)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                secondPanel()
            }
        }
    }
}

/**
 * Arc Scroll Area Component (11 / 70)
 */
@Composable
fun ArcScrollArea(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            content()
        }
    }
}

/**
 * Arc Bottom Sheet Component (12 / 70)
 */
@Composable
fun ArcBottomSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onDismiss() }
            )
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = ArcRadius.panel,
                color = ArcTheme.colors.surface,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(ArcSpacing.space4),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(ArcTheme.colors.borderStrong)
                    )
                    Spacer(modifier = Modifier.height(ArcSpacing.space3))
                    content()
                }
            }
        }
    }
}
