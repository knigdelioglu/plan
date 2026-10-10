package com.plan.app.core.design.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.plan.app.core.design.components.extended.ArcAnimatedCounter
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Block 8 / 9: Stats Band
 * Prominent metric bar showing curriculum statistics and active teacher progress.
 */
@Composable
fun ArcStatsBand(
    totalWeeks: Int = 37,
    totalLessons: Int = 45,
    verifiedHours: Int = 180,
    assumedHours: Int = 185,
    completedHours: Int = 0,
    currentWeekNumber: Int = 1,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    val progressPercent = ((completedHours.toFloat() / assumedHours) * 100).toInt()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ArcRadius.panel,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = ArcSpacing.space6, vertical = ArcSpacing.space4)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stat 1: Hafta
            Column {
                Text(text = "Aktif Hafta", style = ArcTheme.typography.labelSmall, color = colors.textMuted)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ArcAnimatedCounter(targetValue = currentWeekNumber, style = ArcTheme.typography.titleLarge)
                    Text(text = "/ $totalWeeks", style = ArcTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }

            Box(modifier = Modifier.width(1.dp).height(32.dp))

            // Stat 2: Ders Bölümü
            Column {
                Text(text = "Ders Bölümleri", style = ArcTheme.typography.labelSmall, color = colors.textMuted)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "$totalLessons", style = ArcTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.foreground)
                    Text(text = "bölüm (3+2)", style = ArcTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }

            Box(modifier = Modifier.width(1.dp).height(32.dp))

            // Stat 3: Saat Muhasebesi
            Column {
                Text(text = "Yıllık Ders Saati", style = ArcTheme.typography.labelSmall, color = colors.textMuted)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "$verifiedHours + 5", style = ArcTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.foreground)
                    ArcBadge(text = "180 Doğrulandı", variant = ArcBadgeVariant.SUCCESS)
                }
            }

            Box(modifier = Modifier.width(1.dp).height(32.dp))

            // Stat 4: Tamamlanma
            Column {
                Text(text = "Sınıf İlerlemesi", style = ArcTheme.typography.labelSmall, color = colors.textMuted)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ArcAnimatedCounter(
                        targetValue = progressPercent,
                        style = ArcTheme.typography.titleLarge,
                        color = colors.accent,
                        suffix = "%"
                    )
                    Text(text = "($completedHours sa)", style = ArcTheme.typography.bodySmall, color = colors.textSecondary)
                }
            }
        }
    }
}
