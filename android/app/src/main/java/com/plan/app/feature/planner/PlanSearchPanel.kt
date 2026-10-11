package com.plan.app.feature.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.Text
import com.plan.app.core.design.blocks.ArcEmptyStatesBlock
import com.plan.app.core.design.components.advanced.ArcExpandingSearch
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

@Composable
fun PlanSearchPanel(
    query: String,
    results: List<PlanSearchResult>,
    onQueryChange: (String) -> Unit,
    onSelectWeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ArcSpacing.space2)
    ) {
        ArcExpandingSearch(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.fillMaxWidth()
        )
        if (query.isNotBlank()) {
            if (results.isEmpty()) {
                ArcEmptyStatesBlock.SearchResultsEmpty(
                    query = query,
                    onClearQuery = { onQueryChange("") },
                    modifier = Modifier.testTag("plan-search-empty")
                )
            } else {
                ArcCard(modifier = Modifier.fillMaxWidth().testTag("plan-search-results")) {
                    Column(
                        modifier = Modifier.padding(ArcSpacing.space3),
                        verticalArrangement = Arrangement.spacedBy(ArcSpacing.space2)
                    ) {
                        Text(
                            "${results.size} haftada sonuç bulundu",
                            style = ArcTheme.typography.labelSmall,
                            color = colors.textSecondary
                        )
                        results.forEach { result ->
                            val week = result.week
                            ArcButton(
                                onClick = { onSelectWeek(week.weekNumber) },
                                variant = ArcButtonVariant.SUBTLE,
                                modifier = Modifier.fillMaxWidth().testTag("plan-search-week-${week.weekNumber}")
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "${week.weekNumber}. Hafta · ${week.month}",
                                        style = ArcTheme.typography.bodyMedium,
                                        color = colors.foreground
                                    )
                                    Text(
                                        (result.matchingLessons.ifEmpty {
                                            listOfNotNull(week.theme?.themeName)
                                        }).distinct().joinToString(" · ").ifBlank { week.dateRangeRaw },
                                        style = ArcTheme.typography.labelSmall,
                                        color = colors.textSecondary
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
