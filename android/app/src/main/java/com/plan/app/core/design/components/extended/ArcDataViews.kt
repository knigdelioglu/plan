package com.plan.app.core.design.components.extended

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc JSON Viewer Component (58 / 70 - C.3)
 * Syntax-highlighted, expandable tree viewer for inspecting annual-plan.json.
 */
data class JsonNode(
    val key: String,
    val value: String? = null,
    val children: List<JsonNode> = emptyList()
)

@Composable
fun ArcJsonViewer(
    rootNode: JsonNode,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ArcSpacing.space4)
        ) {
            Text(
                text = "Kaynak JSON Denetleyicisi",
                style = ArcTheme.typography.titleMedium,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(ArcSpacing.space3))
            JsonNodeItem(node = rootNode, level = 0)
        }
    }
}

@Composable
private fun JsonNodeItem(
    node: JsonNode,
    level: Int
) {
    var expanded by remember { mutableStateOf(level < 2) }
    val colors = ArcTheme.colors
    val hasChildren = node.children.isNotEmpty()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ArcRadius.xs)
                .clickable { if (hasChildren) expanded = !expanded }
                .padding(start = (level * 16).dp, top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasChildren) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colors.textSecondary
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }

            Text(
                text = "\"${node.key}\": ",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = colors.accent,
                fontWeight = FontWeight.SemiBold
            )

            if (node.value != null) {
                Text(
                    text = node.value,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = colors.success
                )
            } else if (hasChildren) {
                Text(
                    text = if (expanded) "{" else "{ ... }",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = colors.textMuted
                )
            }
        }

        AnimatedVisibility(visible = expanded && hasChildren) {
            Column {
                node.children.forEach { child ->
                    JsonNodeItem(node = child, level = level + 1)
                }
                Row(modifier = Modifier.padding(start = (level * 16 + 16).dp)) {
                    Text(text = "}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = colors.textMuted)
                }
            }
        }
    }
}

/**
 * Arc Sortable Data Table Component (63 / 70 - C.8)
 */
data class TableColumn<T>(
    val title: String,
    val width: Dp = 120.dp,
    val cellContent: @Composable (T) -> Unit,
    val comparator: Comparator<T>? = null
)

@Composable
fun <T> ArcSortableDataTable(
    items: List<T>,
    columns: List<TableColumn<T>>,
    modifier: Modifier = Modifier,
    onRowClick: ((T) -> Unit)? = null
) {
    var sortedColumnIndex by remember { mutableStateOf<Int?>(null) }
    var sortAscending by remember { mutableStateOf(true) }
    val colors = ArcTheme.colors
    val scrollState = rememberScrollState()

    val sortedItems = remember(items, sortedColumnIndex, sortAscending) {
        val col = sortedColumnIndex?.let { columns.getOrNull(it) }
        val comp = col?.comparator
        if (comp != null) {
            if (sortAscending) items.sortedWith(comp) else items.sortedWith(comp.reversed())
        } else items
    }

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.horizontalScroll(scrollState)) {
            // Header Row
            Surface(
                color = colors.surfaceMuted,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    columns.forEachIndexed { idx, col ->
                        Row(
                            modifier = Modifier
                                .width(col.width)
                                .clickable(enabled = col.comparator != null) {
                                    if (sortedColumnIndex == idx) {
                                        sortAscending = !sortAscending
                                    } else {
                                        sortedColumnIndex = idx
                                        sortAscending = true
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = col.title,
                                style = ArcTheme.typography.labelSmall,
                                color = colors.foreground,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (col.comparator != null && sortedColumnIndex == idx) {
                                Icon(
                                    imageVector = if (sortAscending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = colors.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Data Rows
            sortedItems.forEach { item ->
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier.clickable(enabled = onRowClick != null) { onRowClick?.invoke(item) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        columns.forEach { col ->
                            Box(modifier = Modifier.width(col.width)) {
                                col.cellContent(item)
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.borderSubtle)
                )
            }
        }
    }
}
