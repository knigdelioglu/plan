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
import com.knigdelioglu.arc.compose.datavis.ArcStatItem
import com.knigdelioglu.arc.compose.datavis.ArcStatsBand as DesignSystemStatsBand

/**
 * Arc Block 8 / 9: Stats Band
 * Prominent metric bar showing curriculum statistics and active teacher progress.
 */
@Composable
fun ArcStatsBand(
    totalWeeks: Int = 37,
    totalLessons: Int = 45,
    verifiedHours: Int? = null,
    assumedHours: Int? = null,
    annualHours: Int,
    completedHours: Int = 0,
    currentWeekNumber: Int = 1,
    modifier: Modifier = Modifier
) {
    val progressPercent = if (annualHours > 0) ((completedHours.toFloat() / annualHours) * 100).toInt() else 0
    val hoursValue = if (verifiedHours != null && assumedHours != null) {
        "$verifiedHours + $assumedHours"
    } else {
        annualHours.toString()
    }
    val hoursDetail = when {
        verifiedHours != null && assumedHours != null -> "$verifiedHours doğrulanmış · $assumedHours varsayımsal"
        verifiedHours != null -> "$verifiedHours doğrulanmış"
        else -> "Kaynak toplamı"
    }

    DesignSystemStatsBand(
        stats = listOf(
            ArcStatItem("Aktif Hafta", "$currentWeekNumber / $totalWeeks"),
            ArcStatItem("Ders Bölümleri", totalLessons.toString(), "bölüm (3+2)"),
            ArcStatItem("Yıllık Ders Saati", hoursValue, hoursDetail),
            ArcStatItem("Sınıf İlerlemesi", "$progressPercent%", "$completedHours sa tamamlandı")
        ),
        modifier = modifier
    )
}
