package com.plan.app.core.design.components.advanced

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Input Component (31 / 70 - B.8)
 */
@Composable
fun ArcInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    errorText: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val colors = ArcTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = ArcTheme.typography.labelSmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ArcRadius.control,
            color = colors.surface,
            border = BorderStroke(
                1.dp,
                if (errorText != null) colors.danger else if (isFocused) colors.accent else colors.border
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    textStyle = ArcTheme.typography.bodyMedium.copy(color = colors.foreground),
                    cursorBrush = SolidColor(colors.accent),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = ArcTheme.typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                        inner()
                    }
                )
                trailingIcon?.invoke()
            }
        }

        if (errorText != null) {
            Text(
                text = errorText,
                style = ArcTheme.typography.bodySmall,
                color = colors.danger,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

/**
 * Arc Textarea Component (32 / 70 - B.9)
 */
@Composable
fun ArcTextarea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Notlarınızı buraya yazın...",
    minLines: Int = 4,
    maxLines: Int = 10,
    showCharCount: Boolean = true
) {
    val colors = ArcTheme.colors

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = ArcTheme.typography.labelSmall,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ArcRadius.md,
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((minLines * 22).dp),
                    textStyle = ArcTheme.typography.bodyMedium.copy(color = colors.foreground),
                    cursorBrush = SolidColor(colors.accent),
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = ArcTheme.typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                        inner()
                    }
                )

                if (showCharCount) {
                    Text(
                        text = "${value.length} karakter",
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textMuted,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Arc Inline Edit Component (33 / 70 - B.10)
 */
@Composable
fun ArcInlineEdit(
    value: String,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentText by remember(value) { mutableStateOf(value) }
    val colors = ArcTheme.colors

    if (isEditing) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicTextField(
                value = currentText,
                onValueChange = { currentText = it },
                textStyle = ArcTheme.typography.bodyMedium.copy(color = colors.foreground),
                cursorBrush = SolidColor(colors.accent),
                modifier = Modifier
                    .weight(1f)
                    .clip(ArcRadius.sm)
                    .background(colors.surfaceMuted)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onSave(currentText)
                    isEditing = false
                })
            )
            IconButton(
                onClick = {
                    onSave(currentText)
                    isEditing = false
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Kaydet", tint = colors.success)
            }
            IconButton(
                onClick = {
                    currentText = value
                    isEditing = false
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "İptal", tint = colors.danger)
            }
        }
    } else {
        Row(
            modifier = modifier
                .clip(ArcRadius.sm)
                .clickable { isEditing = true }
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                style = ArcTheme.typography.bodyMedium,
                color = colors.foreground
            )
            Icon(
                Icons.Default.Edit,
                contentDescription = "Düzenle",
                tint = colors.textMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Arc Switch Component (34 / 70 - B.5)
 */
@Composable
fun ArcSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = ArcTheme.colors
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 2.dp,
        animationSpec = ArcMotionTokens.Springs.snappy(),
        label = "switch_thumb"
    )

    Surface(
        modifier = modifier
            .width(44.dp)
            .height(26.dp)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        shape = ArcRadius.pill,
        color = if (checked) colors.controlOn else colors.controlTrack
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(colors.controlThumb)
            )
        }
    }
}

/**
 * Arc Radio Group Component (35 / 70 - B.7)
 */
@Composable
fun <T> ArcRadioGroup(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { opt ->
            val isSelected = opt == selected
            val colors = ArcTheme.colors

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(opt) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(20.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, if (isSelected) colors.controlOn else colors.controlTrack),
                    color = Color.Transparent
                ) {
                    if (isSelected) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colors.controlOn)
                            )
                        }
                    }
                }

                Text(
                    text = optionLabel(opt),
                    style = ArcTheme.typography.bodyMedium,
                    color = colors.foreground
                )
            }
        }
    }
}

/**
 * Arc Multi Select Component (36 / 70 - B.6)
 */
@Composable
fun <T> ArcMultiSelect(
    items: List<T>,
    selectedItems: Set<T>,
    onToggle: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    title: String = "Seçilenler"
) {
    Column(modifier = modifier) {
        Text(
            text = "$title (${selectedItems.size})",
            style = ArcTheme.typography.labelSmall,
            color = ArcTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { item ->
                val isSelected = selectedItems.contains(item)
                val colors = ArcTheme.colors

                Surface(
                    modifier = Modifier.clickable { onToggle(item) },
                    shape = ArcRadius.pill,
                    color = if (isSelected) colors.accentSubtle else colors.surfaceMuted,
                    border = BorderStroke(1.dp, if (isSelected) colors.accent else colors.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = colors.accent)
                        }
                        Text(
                            text = itemLabel(item),
                            style = ArcTheme.typography.labelSmall,
                            color = if (isSelected) colors.accent else colors.foreground
                        )
                    }
                }
            }
        }
    }
}

/**
 * Arc Morph Select Component (37 / 70 - B.4)
 * Button morphing into expanded selection panel with spring transition.
 */
@Composable
fun <T> ArcMorphSelect(
    selectedItem: T?,
    items: List<T>,
    onSelect: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    buttonTitle: String = "Görünüm Seç"
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier,
        shape = ArcRadius.control,
        color = colors.surfaceMuted,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (selectedItem != null) itemLabel(selectedItem) else buttonTitle,
                    style = ArcTheme.typography.labelMedium,
                    color = colors.foreground,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    items.forEach { item ->
                        val isSelected = item == selectedItem
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ArcRadius.sm)
                                .background(if (isSelected) colors.surface else Color.Transparent)
                                .clickable {
                                    onSelect(item)
                                    expanded = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = itemLabel(item),
                                style = ArcTheme.typography.bodySmall,
                                color = if (isSelected) colors.accent else colors.foreground,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
