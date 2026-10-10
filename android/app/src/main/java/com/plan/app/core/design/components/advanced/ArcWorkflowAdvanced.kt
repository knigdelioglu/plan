package com.plan.app.core.design.components.advanced

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.motion.LocalReducedMotion
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Tree View Component (49 / 70 - B.1)
 * Hierarchical collapsible tree for Theme -> Week -> Lesson -> Outcomes.
 */
data class TreeNode(
    val id: String,
    val label: String,
    val secondaryText: String? = null,
    val badgeText: String? = null,
    val children: List<TreeNode> = emptyList(),
    val onClick: (() -> Unit)? = null
)

@Composable
fun ArcTreeView(
    nodes: List<TreeNode>,
    modifier: Modifier = Modifier,
    indentPerLevel: Dp = 16.dp
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        nodes.forEach { node ->
            TreeNodeItem(node = node, level = 0, indent = indentPerLevel)
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: TreeNode,
    level: Int,
    indent: Dp
) {
    var expanded by rememberSaveable(node.id) { mutableStateOf(false) }
    val hasChildren = node.children.isNotEmpty()
    val colors = ArcTheme.colors
    val reducedMotion = LocalReducedMotion.current
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = ArcMotionTokens.respectMotionPreference(
            reducedMotion, ArcMotionTokens.Springs.Smooth
        ),
        label = "tree_node_arrow"
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ArcRadius.sm)
                .heightIn(min = 48.dp)
                .semantics {
                    if (hasChildren) stateDescription = if (expanded) "Açık" else "Kapalı"
                }
                .clickable(role = Role.Button) {
                    if (hasChildren) expanded = !expanded
                    node.onClick?.invoke()
                }
                .padding(
                    start = (level * indent.value).dp + 8.dp,
                    end = 8.dp,
                    top = 6.dp,
                    bottom = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (hasChildren) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(arrowRotation),
                    tint = colors.textSecondary
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }

            Text(
                text = node.label,
                style = ArcTheme.typography.bodyMedium,
                color = colors.foreground,
                fontWeight = if (hasChildren) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (node.badgeText != null) {
                ArcBadge(text = node.badgeText, variant = ArcBadgeVariant.NEUTRAL)
            }
        }

        AnimatedVisibility(
            visible = expanded && hasChildren,
            enter = if (reducedMotion) EnterTransition.None else fadeIn(),
            exit = if (reducedMotion) ExitTransition.None else fadeOut()
        ) {
            Column {
                node.children.forEach { child ->
                    TreeNodeItem(node = child, level = level + 1, indent = indent)
                }
            }
        }
    }
}

/**
 * Arc Metric Card Component (50 / 70 - B.16)
 * Value, label, trend badge, subtext.
 */
@Composable
fun ArcMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trendText: String? = null,
    trendPositive: Boolean = true,
    subtext: String? = null
) {
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Text(
                text = label,
                style = ArcTheme.typography.labelSmall,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = value,
                    style = ArcTheme.typography.displayMedium,
                    color = colors.foreground,
                    fontWeight = FontWeight.Bold
                )
                if (trendText != null) {
                    ArcBadge(
                        text = trendText,
                        variant = if (trendPositive) ArcBadgeVariant.SUCCESS else ArcBadgeVariant.WARNING
                    )
                }
            }
            if (subtext != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = ArcTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

/**
 * Arc Carousel Component (51 / 70 - B.17)
 * Horizontal pager with indicators.
 */
@Composable
fun <T> ArcCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val colors = ArcTheme.colors

    Column(modifier = modifier) {
        if (items.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                itemContent(items[currentIndex.coerceIn(0, items.size - 1)])
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.indices.forEach { index ->
                    val isSelected = index == currentIndex
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.accent else colors.borderStrong)
                            .clickable { currentIndex = index }
                    )
                }
            }
        }
    }
}

/**
 * Arc Activity Heatmap Component (52 / 70 - B.18)
 * 37-week curriculum activity heatmap grid.
 */
@Composable
fun ArcActivityHeatmap(
    totalWeeks: Int = 37,
    completedWeeks: Set<Int>,
    currentWeek: Int,
    onSelectWeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Text(
                text = "Haftalara göre ilerleme (${totalWeeks} hafta)",
                style = ArcTheme.typography.titleMedium,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                // Five columns on narrow screens preserve minimum 48dp targets.
                val columns = if (maxWidth < 520.dp) 5 else 10
                val rows = (totalWeeks + columns - 1) / columns
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (col in 0 until columns) {
                                val number = row * columns + col + 1
                                if (number > totalWeeks) {
                                    Spacer(modifier = Modifier.weight(1f))
                                } else {
                                    val done = number in completedWeeks
                                    val selected = number == currentWeek
                                    val cellColor = when {
                                        done -> colors.success
                                        selected -> colors.accent
                                        number < currentWeek -> colors.warningSubtle
                                        else -> colors.surfaceMuted
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 48.dp)
                                            .clip(ArcRadius.xs)
                                            .background(cellColor)
                                            .semantics {
                                                contentDescription = "${number}. hafta, " +
                                                    if (done) "tamamlandı"
                                                    else if (selected) "seçili"
                                                    else "tamamlanmadı"
                                            }
                                            .clickable(role = Role.Button) { onSelectWeek(number) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = number.toString(),
                                            style = ArcTheme.typography.labelSmall,
                                            color = if (done || selected) Color.White else colors.textSecondary,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Arc Timeline Component (53 / 70 - B.19)
 * Vertical academic year timeline with milestone markers.
 */
data class TimelineMilestone(
    val weekNumber: Int,
    val title: String,
    val dateText: String,
    val isBreak: Boolean = false,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false
)

@Composable
fun ArcTimeline(
    milestones: List<TimelineMilestone>,
    onSelectMilestone: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    Column(modifier = modifier) {
        milestones.forEachIndexed { index, m ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectMilestone(m.weekNumber) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle node
                Surface(
                    modifier = Modifier.size(18.dp),
                    shape = CircleShape,
                    color = when {
                        m.isCurrent -> colors.accent
                        m.isBreak -> colors.warning
                        m.isPassed -> colors.success
                        else -> colors.surfaceMuted
                    },
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    if (m.isPassed) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${m.weekNumber}. Hafta — ${m.title}",
                        style = ArcTheme.typography.bodyMedium,
                        fontWeight = if (m.isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = colors.foreground
                    )
                    Text(
                        text = m.dateText,
                        style = ArcTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }

                if (m.isBreak) {
                    ArcBadge(text = "Ara Tatil", variant = ArcBadgeVariant.WARNING)
                } else if (m.isCurrent) {
                    ArcBadge(text = "Aktif Hafta", variant = ArcBadgeVariant.ACCENT)
                }
            }
        }
    }
}

/**
 * Arc Toast Stack Component (54 / 70 - B.22)
 * Stacked toast queue manager.
 */
data class QueuedToast(
    val id: String,
    val message: String
)

@Composable
fun ArcToastStack(
    toasts: List<QueuedToast>,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        toasts.take(3).forEach { t ->
            Surface(
                shape = ArcRadius.pill,
                color = ArcTheme.colors.neutral10,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = t.message,
                        style = ArcTheme.typography.bodySmall,
                        color = ArcTheme.colors.neutral1
                    )
                    IconButton(
                        onClick = { onDismiss(t.id) },
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = ArcTheme.colors.neutral4)
                    }
                }
            }
        }
    }
}

/**
 * Arc Stepper Component (55 / 70 - B.23)
 * Multi-step progress bar/stepper.
 */
@Composable
fun ArcStepper(
    steps: List<String>,
    currentStepIndex: Int,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, title ->
            val isDone = index < currentStepIndex
            val isCurrent = index == currentStepIndex

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = when {
                        isDone -> colors.success
                        isCurrent -> colors.accent
                        else -> colors.surfaceMuted
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isDone) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        } else {
                            Text(
                                text = (index + 1).toString(),
                                style = ArcTheme.typography.labelSmall,
                                color = if (isCurrent) colors.accentForeground else colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    style = ArcTheme.typography.labelSmall,
                    color = if (isCurrent) colors.foreground else colors.textMuted,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .background(if (index < currentStepIndex) colors.success else colors.border)
                )
            }
        }
    }
}
