package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 7 / 9: Comparison Table
 * High-fidelity plan comparison between Anadolu Lisesi and Fen Lisesi curricula.
 */
data class ComparisonRow(
    val feature: String,
    val anadoluValue: String,
    val fenValue: String,
    val isDifferent: Boolean = false
)

@Composable
fun ArcComparisonTable(
    rows: List<ComparisonRow>,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    val scrollState = rememberScrollState()

    ArcCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Text(
                text = "Okul Türü Karşılaştırma Tablosu",
                style = ArcTheme.typography.titleLarge,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(ArcSpacing.space1))
            Text(
                text = "11. Sınıf Türk Dili ve Edebiyatı (Anadolu Lisesi vs Fen Lisesi)",
                style = ArcTheme.typography.bodySmall,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(ArcSpacing.space4))

            Column(modifier = Modifier.horizontalScroll(scrollState)) {
                // Header
                Surface(
                    color = colors.surfaceMuted,
                    shape = ArcRadius.sm,
                    border = BorderStroke(1.dp, colors.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Özellik / Kriter",
                            style = ArcTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.foreground,
                            modifier = Modifier.width(180.dp)
                        )
                        Text(
                            text = "Anadolu Lisesi",
                            style = ArcTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.foreground,
                            modifier = Modifier.width(220.dp)
                        )
                        Text(
                            text = "Fen Lisesi",
                            style = ArcTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.foreground,
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rows
                rows.forEach { row ->
                    Surface(
                        color = if (row.isDifferent) colors.surfaceRaised else colors.surface,
                        border = if (row.isDifferent) BorderStroke(1.dp, colors.accentSubtle) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.feature,
                                style = ArcTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.foreground,
                                modifier = Modifier.width(180.dp)
                            )
                            Text(
                                text = row.anadoluValue,
                                style = ArcTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                modifier = Modifier.width(220.dp)
                            )
                            Row(
                                modifier = Modifier.width(220.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = row.fenValue,
                                    style = ArcTheme.typography.bodySmall,
                                    color = if (row.isDifferent) colors.foreground else colors.textSecondary,
                                    fontWeight = if (row.isDifferent) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                if (row.isDifferent) {
                                    ArcBadge(text = "Fark", variant = ArcBadgeVariant.WARNING)
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}
