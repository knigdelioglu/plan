package com.plan.app.core.design.components.advanced

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.components.core.ArcSearchField
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Arc Context Menu Component (40 / 70 - B.11)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArcContextMenu(
    items: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.combinedClickable(
            onClick = {},
            onLongClick = { expanded = true }
        )
    ) {
        content()

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { (title, action) ->
                DropdownMenuItem(
                    text = { Text(title, style = ArcTheme.typography.bodyMedium) },
                    onClick = {
                        action()
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Arc Copy Button Component (41 / 70 - B.12)
 * One-click copy with morphing checkmark feedback.
 */
@Composable
fun ArcCopyButton(
    textToCopy: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier
            .clip(ArcRadius.pill)
            .clickable {
                onCopy(textToCopy)
                copied = true
                scope.launch {
                    delay(1800)
                    copied = false
                }
            },
        shape = ArcRadius.pill,
        color = if (copied) colors.successSubtle else colors.surfaceMuted,
        border = BorderStroke(1.dp, if (copied) colors.success else colors.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = if (copied) "Kopyalandı" else "Kopyala",
                modifier = Modifier.size(14.dp),
                tint = if (copied) colors.success else colors.textSecondary
            )
            Text(
                text = if (copied) "Kopyalandı" else "Kopyala",
                style = ArcTheme.typography.labelSmall,
                color = if (copied) colors.success else colors.textSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Arc Theme Switch Component (42 / 70 - B.13)
 * Sun/Moon morph switch.
 */
@Composable
fun ArcThemeSwitch(
    isDark: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    val offset by animateDpAsState(
        targetValue = if (isDark) 24.dp else 2.dp,
        animationSpec = ArcMotionTokens.Springs.morph(),
        label = "theme_switch_offset"
    )

    Surface(
        modifier = modifier
            .width(52.dp)
            .height(30.dp)
            .clickable { onToggle() },
        shape = ArcRadius.pill,
        color = colors.surfaceMuted,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                modifier = Modifier
                    .offset(x = offset)
                    .size(26.dp),
                shape = CircleShape,
                color = colors.surface,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = if (isDark) "Koyu Tema" else "Açık Tema",
                        tint = colors.accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Arc Swipe Actions Component (43 / 70 - B.14)
 * Swipe left/right wrapper for list items.
 */
@Composable
fun ArcSwipeActions(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val triggerPx = with(density) { 72.dp.toPx() }
    val maxOffset = triggerPx * 1.5f
    val animatedOffset by animateFloatAsState(targetValue = offsetX, label = "ArcSwipeOffset")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(onSwipeLeft, onSwipeRight, triggerPx) {
                detectHorizontalDragGestures(
                    onDragStart = { offsetX = 0f },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        offsetX = (offsetX + amount).coerceIn(-maxOffset, maxOffset)
                    },
                    onDragEnd = {
                        val result = offsetX
                        offsetX = 0f
                        if (result <= -triggerPx) onSwipeLeft()
                        else if (result >= triggerPx) onSwipeRight()
                    },
                    onDragCancel = { offsetX = 0f }
                )
            }
    ) {
        Box(modifier = Modifier.offset { IntOffset(animatedOffset.roundToInt(), 0) }) {
            content()
        }
    }
}

/**
 * Arc Expanding Search Component (44 / 70 - B.15)
 * Search icon expanding into full search bar on click.
 */
@Composable
fun ArcExpandingSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.animateContentSize(animationSpec = ArcMotionTokens.Springs.morph()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!expanded) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ara",
                    tint = ArcTheme.colors.textSecondary
                )
            }
        } else {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArcSearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        expanded = false
                        onQueryChange("")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Kapat",
                        tint = ArcTheme.colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Arc Confirm Morph Component (45 / 70 - B.20)
 * Button that morphs into "Emin misiniz? Onayla / İptal" confirmation.
 */
@Composable
fun ArcConfirmMorph(
    promptText: String = "Sil",
    confirmText: String = "Onayla",
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirming by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.animateContentSize(animationSpec = ArcMotionTokens.Springs.morph()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (!confirming) {
            ArcButton(
                onClick = { confirming = true },
                variant = ArcButtonVariant.SECONDARY
            ) {
                Text(promptText)
            }
        } else {
            ArcButton(
                onClick = {
                    onConfirm()
                    confirming = false
                },
                variant = ArcButtonVariant.DANGER
            ) {
                Text(confirmText)
            }
            ArcButton(
                onClick = { confirming = false },
                variant = ArcButtonVariant.SUBTLE
            ) {
                Text("İptal")
            }
        }
    }
}

/**
 * Arc Hold To Confirm Component (46 / 70 - B.21)
 * Press-and-hold circular progress ring button for critical actions.
 */
@Composable
fun ArcHoldToConfirm(
    label: String,
    onConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
    holdDurationMillis: Long = 1200L
) {
    var isHolding by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier
            .clip(ArcRadius.control)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        progress = 0f
                        val startTime = System.currentTimeMillis()
                        while (isHolding && (System.currentTimeMillis() - startTime) < holdDurationMillis) {
                            progress = ((System.currentTimeMillis() - startTime).toFloat() / holdDurationMillis)
                            delay(16)
                        }
                        if (isHolding && progress >= 0.95f) {
                            onConfirmed()
                        }
                        isHolding = false
                        progress = 0f
                    }
                )
            },
        shape = ArcRadius.control,
        color = if (isHolding) colors.dangerSubtle else colors.surfaceMuted,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isHolding) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(16.dp),
                    color = colors.danger,
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = if (isHolding) "Basılı tutun..." else label,
                style = ArcTheme.typography.labelMedium,
                color = if (isHolding) colors.danger else colors.foreground
            )
        }
    }
}

/**
 * Arc Split Button Component (47 / 70 - B.24)
 * Main action button combined with dropdown chevron.
 */
@Composable
fun ArcSplitButton(
    mainLabel: String,
    onMainClick: () -> Unit,
    menuItems: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier,
        shape = ArcRadius.control,
        color = colors.accent
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clickable { onMainClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = mainLabel,
                    style = ArcTheme.typography.labelMedium,
                    color = colors.accentForeground
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(colors.accentForeground.copy(alpha = 0.2f))
            )

            Box(
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Diğer işlemler",
                    tint = colors.accentForeground,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItems.forEach { (label, action) ->
                    DropdownMenuItem(
                        text = { Text(label, style = ArcTheme.typography.bodyMedium) },
                        onClick = {
                            action()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Arc Floating Button Group Component (48 / 70 - B.25)
 * Floating action bar with quick tools: Note, Pen, Mark Done, Search.
 */
@Composable
fun ArcFloatingButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = ArcRadius.pill,
        color = ArcTheme.colors.surfaceRaised,
        border = BorderStroke(1.dp, ArcTheme.colors.border),
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}
