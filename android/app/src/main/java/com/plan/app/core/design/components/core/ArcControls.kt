package com.plan.app.core.design.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Segmented Control Component (19 / 70)
 */
@Composable
fun <T> ArcSegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    title: (T) -> String,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier,
        shape = ArcRadius.pill,
        color = colors.surfaceMuted,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            options.forEach { opt ->
                val isSelected = opt == selected
                Box(
                    modifier = Modifier
                        .clip(ArcRadius.pill)
                        .background(if (isSelected) colors.surface else Color.Transparent)
                        .clickable { onSelect(opt) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title(opt),
                        style = ArcTheme.typography.labelMedium,
                        color = if (isSelected) colors.foreground else colors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Arc Select Component (20 / 70)
 */
@Composable
fun <T> ArcSelect(
    selectedItem: T?,
    items: List<T>,
    onSelect: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String = "Seçiniz"
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = ArcTheme.colors

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = ArcRadius.control,
            color = colors.surface,
            border = BorderStroke(1.dp, if (expanded) colors.accent else colors.border)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (selectedItem != null) itemLabel(selectedItem) else placeholder,
                    style = ArcTheme.typography.bodyMedium,
                    color = if (selectedItem != null) colors.foreground else colors.textMuted
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemLabel(item),
                            style = ArcTheme.typography.bodyMedium,
                            color = if (isSelected) colors.accent else colors.foreground,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    trailingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = colors.accent) }
                    } else null,
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Arc Combobox Component (21 / 70) - Searchable Select
 */
@Composable
fun <T> ArcCombobox(
    items: List<T>,
    selectedItem: T?,
    onSelect: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String = "Ara ve seç..."
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val colors = ArcTheme.colors

    val filteredItems = remember(query, items) {
        if (query.isBlank()) items else items.filter { itemLabel(it).contains(query, ignoreCase = true) }
    }

    Column(modifier = modifier) {
        ArcSearchField(
            query = query,
            onQueryChange = {
                query = it
                expanded = true
            },
            placeholder = if (selectedItem != null) itemLabel(selectedItem) else placeholder
        )

        if (expanded && filteredItems.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = ArcRadius.md,
                color = colors.surface,
                border = BorderStroke(1.dp, colors.border),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    filteredItems.take(6).forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(item)
                                    query = ""
                                    expanded = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = itemLabel(item),
                                style = ArcTheme.typography.bodyMedium,
                                color = colors.foreground
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Arc Filter Toolbar Component (22 / 70)
 */
@Composable
fun ArcFilterToolbar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ArcTheme.colors.surfaceMuted,
        shape = ArcRadius.control,
        border = BorderStroke(1.dp, ArcTheme.colors.border)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            content()
        }
    }
}

/**
 * Arc Chip Group Component (23 / 70)
 */
@Composable
fun <T> ArcChipGroup(
    items: List<T>,
    selectedItems: Set<T>,
    onToggle: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = selectedItems.contains(item)
            val colors = ArcTheme.colors

            Surface(
                modifier = Modifier.clickable { onToggle(item) },
                shape = ArcRadius.pill,
                color = if (isSelected) colors.accent else colors.surface,
                border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.border)
            ) {
                Text(
                    text = itemLabel(item),
                    style = ArcTheme.typography.labelSmall,
                    color = if (isSelected) colors.accentForeground else colors.foreground,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Arc Search Field Component (24 / 70)
 */
@Composable
fun ArcSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Planda ara...",
    onSearchCommit: (() -> Unit)? = null
) {
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ArcRadius.pill,
        color = colors.surfaceMuted,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Ara",
                tint = colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = ArcTheme.typography.bodyMedium.copy(color = colors.foreground),
                cursorBrush = SolidColor(colors.accent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchCommit?.invoke() }),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = ArcTheme.typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Temizle",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Arc Checkbox Component (25 / 70)
 */
@Composable
fun ArcCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = ArcTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.85f,
        animationSpec = ArcMotionTokens.Springs.Snappy,
        label = "checkbox_scale"
    )

    Surface(
        modifier = modifier
            .size(22.dp)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        shape = RoundedCornerShape(6.dp),
        color = if (checked) colors.controlOn else Color.Transparent,
        border = BorderStroke(1.5.dp, if (checked) colors.controlOn else colors.controlTrack)
    ) {
        if (checked) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colors.controlGlyph,
                    modifier = Modifier
                        .size(15.dp)
                        .scale(scale)
                )
            }
        }
    }
}

/**
 * Arc Dropdown Menu Component (26 / 70)
 */
@Composable
fun ArcDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.background(ArcTheme.colors.surface)
    ) {
        content()
    }
}

/**
 * Arc Dialog Component (27 / 70)
 */
@Composable
fun ArcDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (isOpen) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .padding(ArcSpacing.space4),
                shape = ArcRadius.panel,
                color = ArcTheme.colors.surface,
                border = BorderStroke(1.dp, ArcTheme.colors.border),
                shadowElevation = 24.dp
            ) {
                Column(modifier = Modifier.padding(ArcSpacing.space6)) {
                    Text(
                        text = title,
                        style = ArcTheme.typography.titleLarge,
                        color = ArcTheme.colors.foreground
                    )
                    Spacer(modifier = Modifier.height(ArcSpacing.space3))
                    content()
                    Spacer(modifier = Modifier.height(ArcSpacing.space5))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dismissButton?.invoke()
                        if (dismissButton != null && confirmButton != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        confirmButton?.invoke()
                    }
                }
            }
        }
    }
}

/**
 * Arc Alert Component (28 / 70)
 */
enum class ArcAlertVariant {
    INFO,
    WARNING,
    DANGER,
    SUCCESS
}

@Composable
fun ArcAlert(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    variant: ArcAlertVariant = ArcAlertVariant.INFO,
    action: (@Composable () -> Unit)? = null
) {
    val colors = ArcTheme.colors
    val (bgColor, accentColor, icon) = when (variant) {
        ArcAlertVariant.INFO -> Triple(colors.accentSubtle, colors.accent, Icons.Default.Info)
        ArcAlertVariant.WARNING -> Triple(colors.warningSubtle, colors.warning, Icons.Default.Warning)
        ArcAlertVariant.DANGER -> Triple(colors.dangerSubtle, colors.danger, Icons.Default.Close)
        ArcAlertVariant.SUCCESS -> Triple(colors.successSubtle, colors.success, Icons.Default.Check)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ArcRadius.control,
        color = bgColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(ArcSpacing.space4),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(ArcSpacing.space3)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = ArcTheme.typography.titleMedium,
                    color = colors.foreground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message,
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
                if (action != null) {
                    Spacer(modifier = Modifier.height(ArcSpacing.space2))
                    action()
                }
            }
        }
    }
}

/**
 * Arc Toast Component (29 / 70)
 */
@Composable
fun ArcToast(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier,
        shape = ArcRadius.pill,
        color = ArcTheme.colors.neutral10,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = ArcTheme.typography.bodySmall,
                color = ArcTheme.colors.neutral1
            )
            if (actionLabel != null && onActionClick != null) {
                Text(
                    text = actionLabel,
                    style = ArcTheme.typography.labelSmall,
                    color = ArcTheme.colors.accentForeground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onActionClick() }
                )
            }
        }
    }
}
