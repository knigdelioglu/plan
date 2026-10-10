package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 1 / 9: Page Header
 * Collapsible / sticky page header with breadcrumb, title, subtitle, status badge, and actions.
 */
@Composable
fun ArcPageHeader(
    title: String,
    modifier: Modifier = Modifier,
    breadcrumb: String? = null,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeVariant: ArcBadgeVariant = ArcBadgeVariant.ACCENT,
    actions: (@Composable () -> Unit)? = null
) {
    val colors = ArcTheme.colors

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.borderSubtle),
        shape = ArcRadius.panel
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ArcSpacing.space6, vertical = ArcSpacing.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (breadcrumb != null) {
                    Text(
                        text = breadcrumb,
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = ArcTheme.typography.displayMedium,
                        color = colors.foreground
                    )
                    if (badgeText != null) {
                        ArcBadge(text = badgeText, variant = badgeVariant)
                    }
                }
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = ArcTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            if (actions != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actions()
                }
            }
        }
    }
}
