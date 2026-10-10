package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcSearchField
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 2 / 9: Command Palette
 * Quick navigation launcher dialog with keyboard shortcut (Cmd+K / Ctrl+K).
 */
data class CommandItem(
    val id: String,
    val title: String,
    val category: String,
    val shortcut: String? = null,
    val icon: ImageVector = Icons.Default.Search,
    val onExecute: () -> Unit
)

@Composable
fun ArcCommandPalette(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    commands: List<CommandItem>,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    var query by remember(isOpen) { mutableStateOf("") }
    val inputFocus = remember { FocusRequester() }
    val colors = ArcTheme.colors

    val filteredCommands = remember(query, commands) {
        if (query.isBlank()) commands else commands.filter {
            it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        LaunchedEffect(Unit) { inputFocus.requestFocus() }
        Surface(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(ArcSpacing.space4),
            shape = ArcRadius.panel,
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 24.dp
        ) {
            Column(modifier = Modifier.padding(ArcSpacing.space4)) {
                ArcSearchField(
                    query = query,
                    onQueryChange = { query = it },
                    placeholder = "Hafta veya görünüm ara...",
                    focusRequester = inputFocus
                )

                Spacer(modifier = Modifier.height(ArcSpacing.space3))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredCommands) { cmd ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ArcRadius.sm)
                                .heightIn(min = 48.dp)
                                .clickable(role = Role.Button) {
                                    cmd.onExecute()
                                    onDismiss()
                                },
                            color = colors.surfaceMuted
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = cmd.icon,
                                        contentDescription = null,
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = cmd.title,
                                            style = ArcTheme.typography.bodyMedium,
                                            color = colors.foreground
                                        )
                                        Text(
                                            text = cmd.category,
                                            style = ArcTheme.typography.labelSmall,
                                            color = colors.textMuted
                                        )
                                    }
                                }

                                if (cmd.shortcut != null) {
                                    ArcBadge(text = cmd.shortcut, variant = ArcBadgeVariant.NEUTRAL)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = colors.textMuted,
                                        modifier = Modifier.size(16.dp)
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
