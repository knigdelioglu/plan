package com.plan.app.core.design.components.extended

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.components.core.ArcActionButton
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Expanding Button Group Component (56 / 70 - C.1)
 * Collapsed FAB expanding into radial/vertical action buttons.
 */
data class ExpandingAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun ArcExpandingButtonGroup(
    actions: List<ExpandingAction>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = ArcMotionTokens.Springs.Morph,
        label = "fab_rotation"
    )
    val colors = ArcTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(visible = expanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.forEach { action ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = ArcRadius.sm,
                            color = colors.surfaceRaised,
                            border = BorderStroke(1.dp, colors.border),
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = action.label,
                                style = ArcTheme.typography.labelSmall,
                                color = colors.foreground,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            onClick = {
                                action.onClick()
                                expanded = false
                            },
                            shape = CircleShape,
                            color = colors.accent,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.label,
                                    tint = colors.accentForeground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main FAB button
        Surface(
            onClick = { expanded = !expanded },
            shape = CircleShape,
            color = colors.accent,
            shadowElevation = 8.dp,
            modifier = Modifier.size(54.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (expanded) "Kapat" else "İşlemler",
                    tint = colors.accentForeground,
                    modifier = Modifier
                        .size(26.dp)
                        .rotate(rotation)
                )
            }
        }
    }
}

/**
 * Arc Card Stack Component (57 / 70 - C.2)
 * Stacked interactive swipeable cards for weekly review.
 */
@Composable
fun <T> ArcCardStack(
    cards: List<T>,
    onCardDismiss: (T) -> Unit,
    modifier: Modifier = Modifier,
    cardContent: @Composable (T) -> Unit
) {
    var topIndex by remember { mutableIntStateOf(0) }
    val colors = ArcTheme.colors

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (topIndex < cards.size) {
            val visibleCards = cards.subList(topIndex, minOf(topIndex + 3, cards.size))
            visibleCards.reversed().forEachIndexed { reverseIdx, item ->
                val depth = visibleCards.size - 1 - reverseIdx
                val scale = 1f - (depth * 0.05f)
                val offsetY = (depth * 10).dp

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .offset(y = offsetY)
                        .scale(scale)
                        .clickable {
                            onCardDismiss(item)
                            topIndex++
                        },
                    shape = ArcRadius.panel,
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.border),
                    shadowElevation = (6 - depth * 2).dp
                ) {
                    Box(modifier = Modifier.padding(ArcSpacing.space4)) {
                        cardContent(item)
                    }
                }
            }
        } else {
            ArcCard(modifier = Modifier.fillMaxWidth(0.92f)) {
                Box(modifier = Modifier.padding(ArcSpacing.space6), contentAlignment = Alignment.Center) {
                    Text("Tüm haftalar incelendi!", style = ArcTheme.typography.titleMedium, color = colors.foreground)
                }
            }
        }
    }
}

/**
 * Arc Pagination Component (59 / 70 - C.4)
 */
@Composable
fun ArcPagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = currentPage > 1,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Önceki", tint = colors.foreground)
        }

        Text(
            text = "$currentPage / $totalPages",
            style = ArcTheme.typography.labelMedium,
            color = colors.foreground,
            fontWeight = FontWeight.SemiBold
        )

        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Sonraki", tint = colors.foreground)
        }
    }
}

/**
 * Arc User Menu Component (70 / 70 - C.15)
 * Workspace / Profile / Settings dropdown menu.
 */
@Composable
fun ArcUserMenu(
    userName: String = "Öğretmen Çalışma Alanı",
    userRole: String = "11. Sınıf TDE",
    menuOptions: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = ArcTheme.colors

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .clip(ArcRadius.pill)
                .clickable { expanded = true },
            shape = ArcRadius.pill,
            color = colors.surfaceMuted,
            border = BorderStroke(1.dp, colors.border)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = colors.accent,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = colors.accentForeground,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Column {
                    Text(text = userName, style = ArcTheme.typography.labelSmall, color = colors.foreground, fontWeight = FontWeight.SemiBold)
                    Text(text = userRole, style = ArcTheme.typography.bodySmall, fontSize = 10.sp, color = colors.textMuted)
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuOptions.forEach { (title, action) ->
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
