package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.NotificationItem
import com.plan.app.core.model.NotificationType

/**
 * Arc Block 4 / 9: Notification Center
 * Notification panel for plan updates, academic calendar breaks, and verification notes.
 */
@Composable
fun ArcNotificationCenter(
    notifications: List<NotificationItem>,
    onDismissNotification: (String) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(ArcSpacing.space5)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = colors.accent
                    )
                    Text(
                        text = "Bildirim Merkezi",
                        style = ArcTheme.typography.titleLarge,
                        color = colors.foreground
                    )
                    ArcBadge(
                        text = notifications.size.toString(),
                        variant = ArcBadgeVariant.ACCENT
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            if (notifications.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Tümünü Temizle",
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        modifier = Modifier.clickable { onClearAll() }
                    )
                }
                Spacer(modifier = Modifier.height(ArcSpacing.space2))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { item ->
                    ArcCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(ArcSpacing.space3),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.title,
                                        style = ArcTheme.typography.titleMedium,
                                        color = colors.foreground
                                    )
                                    val variant = when (item.type) {
                                        NotificationType.ACADEMIC_BREAK -> ArcBadgeVariant.WARNING
                                        NotificationType.SPECIAL_DAY -> ArcBadgeVariant.SUCCESS
                                        NotificationType.VALIDATION_NOTICE -> ArcBadgeVariant.ACCENT
                                        else -> ArcBadgeVariant.NEUTRAL
                                    }
                                    ArcBadge(text = item.type.name, variant = variant)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.message,
                                    style = ArcTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.timestamp,
                                    style = ArcTheme.typography.labelSmall,
                                    color = colors.textMuted
                                )
                            }

                            IconButton(
                                onClick = { onDismissNotification(item.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Kaldır", tint = colors.textMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
