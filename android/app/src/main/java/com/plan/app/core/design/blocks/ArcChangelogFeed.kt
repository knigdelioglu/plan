package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.ChangelogEntry

/**
 * Arc Block 6 / 9: Changelog Feed
 * Curriculum data version and verification changelog feed.
 */
@Composable
fun ArcChangelogFeed(
    entries: List<ChangelogEntry>,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(ArcSpacing.space5)) {
            Text(
                text = "Plan Arşiv Değişiklik Günlüğü",
                style = ArcTheme.typography.titleLarge,
                color = colors.foreground
            )

            Spacer(modifier = Modifier.height(ArcSpacing.space4))

            entries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(28.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(12.dp),
                            shape = CircleShape,
                            color = colors.accent
                        ) {}
                        if (index < entries.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(90.dp)
                                    .background(colors.border)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ArcBadge(text = entry.version, variant = ArcBadgeVariant.ACCENT)
                            Text(
                                text = entry.date,
                                style = ArcTheme.typography.labelSmall,
                                color = colors.textMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = entry.title,
                            style = ArcTheme.typography.titleMedium,
                            color = colors.foreground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        entry.details.forEach { detail ->
                            Text(
                                text = "• $detail",
                                style = ArcTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(ArcSpacing.space4))
                    }
                }
            }
        }
    }
}
