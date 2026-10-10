package com.plan.app.core.design.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Card Component (4 / 70)
 */
@Composable
fun ArcCard(
    modifier: Modifier = Modifier,
    shape: Shape = ArcRadius.panel,
    backgroundColor: Color = ArcTheme.colors.surface,
    borderColor: Color = ArcTheme.colors.border,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = elevation,
        onClick = onClick ?: {},
        enabled = onClick != null
    ) {
        content()
    }
}

/**
 * Arc Expandable Card Component (5 / 70)
 */
@Composable
fun ArcExpandableCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    initiallyExpanded: Boolean = false,
    badge: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable(title) { mutableStateOf(initiallyExpanded) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = ArcMotionTokens.Springs.Smooth,
        label = "expand_arrow"
    )

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(ArcSpacing.space4)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .semantics { stateDescription = if (expanded) "Genişletilmiş" else "Daraltılmış" }
                    .clickable(role = Role.Button) { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ArcSpacing.space3),
                    modifier = Modifier.weight(1f)
                ) {
                    leadingIcon?.invoke()
                    Column {
                        Text(
                            text = title,
                            style = ArcTheme.typography.titleMedium,
                            color = ArcTheme.colors.foreground
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = ArcTheme.typography.bodySmall,
                                color = ArcTheme.colors.textSecondary
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ArcSpacing.space2)
                ) {
                    badge?.invoke()
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Daralt" else "Genişlet",
                        modifier = Modifier.rotate(arrowRotation),
                        tint = ArcTheme.colors.textSecondary
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(animationSpec = ArcMotionTokens.Springs.smooth()),
                exit = fadeOut() + shrinkVertically(animationSpec = ArcMotionTokens.Springs.smooth())
            ) {
                Column(modifier = Modifier.padding(top = ArcSpacing.space3)) {
                    content()
                }
            }
        }
    }
}

/**
 * Arc Accordion Component (6 / 70)
 */
data class AccordionItem(
    val id: String,
    val title: String,
    val content: @Composable () -> Unit
)

@Composable
fun ArcAccordion(
    items: List<AccordionItem>,
    modifier: Modifier = Modifier,
    allowMultiple: Boolean = false
) {
    var expandedIds by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ArcSpacing.space2)
    ) {
        items.forEach { item ->
            val isExpanded = expandedIds.contains(item.id)
            val arrowRotation by animateFloatAsState(
                targetValue = if (isExpanded) 180f else 0f,
                animationSpec = ArcMotionTokens.Springs.Smooth,
                label = "accordion_arrow_${item.id}"
            )

            ArcCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(ArcSpacing.space4)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .semantics { stateDescription = if (isExpanded) "Genişletilmiş" else "Daraltılmış" }
                            .clickable(role = Role.Button) {
                                expandedIds = if (isExpanded) {
                                    expandedIds - item.id
                                } else {
                                    if (allowMultiple) expandedIds + item.id else setOf(item.id)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.title,
                            style = ArcTheme.typography.titleMedium,
                            color = ArcTheme.colors.foreground
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(arrowRotation),
                            tint = ArcTheme.colors.textSecondary
                        )
                    }

                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn() + expandVertically(animationSpec = ArcMotionTokens.Springs.smooth()),
                        exit = fadeOut() + shrinkVertically(animationSpec = ArcMotionTokens.Springs.smooth())
                    ) {
                        Column(modifier = Modifier.padding(top = ArcSpacing.space3)) {
                            item.content()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Arc Radio Cards Component (7 / 70)
 */
data class RadioCardOption<T>(
    val value: T,
    val title: String,
    val description: String? = null,
    val tag: String? = null
)

@Composable
fun <T> ArcRadioCards(
    options: List<RadioCardOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ArcSpacing.space2)
    ) {
        options.forEach { opt ->
            val isSelected = opt.value == selected
            val colors = ArcTheme.colors

            ArcCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(opt.value) },
                shape = ArcRadius.control,
                backgroundColor = if (isSelected) colors.surfaceRaised else colors.surface,
                borderColor = if (isSelected) colors.accent else colors.border,
                borderWidth = if (isSelected) 2.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(ArcSpacing.space3),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opt.title,
                            style = ArcTheme.typography.titleMedium,
                            color = colors.foreground
                        )
                        if (opt.description != null) {
                            Text(
                                text = opt.description,
                                style = ArcTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                    if (isSelected) {
                        Surface(
                            modifier = Modifier.size(22.dp),
                            shape = CircleShape,
                            color = colors.accent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Seçili",
                                    tint = colors.accentForeground,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
