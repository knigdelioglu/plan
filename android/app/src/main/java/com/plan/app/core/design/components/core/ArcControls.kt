package com.plan.app.core.design.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
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
import com.plan.app.core.design.motion.LocalReducedMotion
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.knigdelioglu.arc.compose.controls.ArcCheckbox as DesignSystemCheckbox
import com.knigdelioglu.arc.compose.controls.ArcInput as DesignSystemInput
import com.knigdelioglu.arc.compose.controls.ArcSelect as DesignSystemSelect
import com.knigdelioglu.arc.compose.controls.ArcSegmentedControl as DesignSystemSegmentedControl
import com.knigdelioglu.arc.compose.display.ArcAlert as DesignSystemAlert
import com.knigdelioglu.arc.compose.display.ArcAlertVariant as DesignSystemAlertVariant
import com.knigdelioglu.arc.compose.display.ArcDialog as DesignSystemDialog

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
    DesignSystemSegmentedControl(
        items = options.map(title),
        selectedIndex = options.indexOf(selected),
        onSelectIndex = { index -> options.getOrNull(index)?.let(onSelect) },
        modifier = modifier
    )
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
    DesignSystemSelect(
        selectedItem = selectedItem,
        items = items,
        onItemSelected = onSelect,
        modifier = modifier,
        placeholder = placeholder,
        labelProvider = itemLabel
    )
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
    onSearchCommit: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null
) {
    DesignSystemInput(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Ara", tint = ArcTheme.colors.textSecondary)
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Temizle", tint = ArcTheme.colors.textSecondary)
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchCommit?.invoke() }),
        inputModifier = focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
    )
}

/**
 * Arc Checkbox Component (25 / 70)
 */
@Composable
fun ArcCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null
) {
    DesignSystemCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.widthIn(min = 48.dp),
        enabled = enabled,
        label = label
    )
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
    DesignSystemDialog(
        isOpen = isOpen,
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = modifier,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        content = content
    )
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
    DesignSystemAlert(
        title = title,
        message = message,
        modifier = modifier,
        variant = when (variant) {
            ArcAlertVariant.INFO -> DesignSystemAlertVariant.Info
            ArcAlertVariant.WARNING -> DesignSystemAlertVariant.Warning
            ArcAlertVariant.DANGER -> DesignSystemAlertVariant.Danger
            ArcAlertVariant.SUCCESS -> DesignSystemAlertVariant.Success
        },
        action = action
    )
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
