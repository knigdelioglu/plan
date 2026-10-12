package com.plan.app.feature.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.LearningOutcome

/** Learning outcomes remain visible even when the supporting lesson details are collapsed. */
@Composable
internal fun LessonLearningOutcomes(
    outcomes: List<LearningOutcome>,
    modifier: Modifier = Modifier
) {
    if (outcomes.isEmpty()) return

    val colors = ArcTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Öğrenme Çıktıları",
            style = ArcTheme.typography.labelSmall,
            color = colors.textSecondary
        )
        outcomes.forEach { outcome ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (outcome.code.isNotBlank()) {
                    ArcBadge(text = outcome.code, variant = ArcBadgeVariant.ACCENT)
                }
                Text(
                    text = outcome.text,
                    modifier = Modifier.weight(1f),
                    style = ArcTheme.typography.bodySmall,
                    color = colors.foreground
                )
            }
        }
    }
}
