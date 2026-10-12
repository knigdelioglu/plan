package com.plan.app.feature.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.advanced.ArcThemeSwitch
import com.plan.app.core.design.components.core.ArcActionButton
import com.plan.app.core.design.components.core.ArcBadge
import com.plan.app.core.design.components.core.ArcBadgeVariant
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.FontScalePreference

/** Changes use the same atomic teacher-state snapshot as the other preferences. */
@Composable
internal fun PlannerSettingsScreen(
    fontScalePercent: Int,
    onFontScaleChange: (Int) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onBack: () -> Unit
) {
    val colors = ArcTheme.colors
    Box(
        modifier = Modifier.fillMaxSize().background(colors.background).testTag("plan-settings"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 720.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings-back-button")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Plana dön", tint = colors.foreground)
                }
                Text("Ayarlar", style = ArcTheme.typography.titleLarge, color = colors.foreground)
            }

            ArcCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Yazı boyutu", style = ArcTheme.typography.titleMedium, color = colors.foreground)
                    Text(
                        "Plan, öğrenme çıktıları ve düğmelerdeki yazıları büyütün veya küçültün.",
                        style = ArcTheme.typography.bodyMedium, color = colors.textSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ArcActionButton(
                            onClick = { onFontScaleChange(fontScalePercent - FontScalePreference.STEP_PERCENT) },
                            enabled = fontScalePercent > FontScalePreference.MIN_PERCENT,
                            modifier = Modifier.testTag("font-scale-decrease").semantics {
                                contentDescription = "Yazıyı küçült"
                            }
                        ) { Text("A−") }
                        Text(
                            "%$fontScalePercent",
                            style = ArcTheme.typography.titleLarge, color = colors.foreground,
                            modifier = Modifier.testTag("font-scale-value")
                        )
                        ArcActionButton(
                            onClick = { onFontScaleChange(fontScalePercent + FontScalePreference.STEP_PERCENT) },
                            enabled = fontScalePercent < FontScalePreference.MAX_PERCENT,
                            modifier = Modifier.testTag("font-scale-increase").semantics {
                                contentDescription = "Yazıyı büyüt"
                            }
                        ) { Text("A+") }
                    }
                    Text(
                        "En küçük %90 · Önerilen %115 · En büyük %140. Cihazın erişilebilirlik yazı ayarı da korunur.",
                        style = ArcTheme.typography.bodySmall, color = colors.textSecondary
                    )
                    ArcButton(
                        onClick = { onFontScaleChange(FontScalePreference.DEFAULT_PERCENT) },
                        enabled = fontScalePercent != FontScalePreference.DEFAULT_PERCENT,
                        variant = ArcButtonVariant.SUBTLE,
                        modifier = Modifier.testTag("font-scale-reset")
                    ) { Text("Önerilen boyuta dön") }
                }
            }

            ArcCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Görünüm", style = ArcTheme.typography.titleMedium, color = colors.foreground)
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Koyu tema", style = ArcTheme.typography.bodyMedium, color = colors.foreground)
                        ArcThemeSwitch(isDark = isDarkMode, onToggle = onToggleDarkMode)
                    }
                }
            }

            ArcCard(modifier = Modifier.fillMaxWidth().testTag("font-scale-preview")) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Önizleme", style = ArcTheme.typography.labelMedium, color = colors.textSecondary)
                    Text("1. Bölüm: KONUŞMA", style = ArcTheme.typography.titleMedium, color = colors.foreground)
                    Text("Öğrenme Çıktıları", style = ArcTheme.typography.labelSmall, color = colors.textSecondary)
                    Row(
                        verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ArcBadge(text = "TDE3.3", variant = ArcBadgeVariant.ACCENT)
                        Text(
                            "Sözlü iletişim engellerine ilişkin farklı bakış açılarını deneyimlediği konuşmasında kural uygulayabilme",
                            modifier = Modifier.weight(1f),
                            style = ArcTheme.typography.bodySmall, color = colors.foreground
                        )
                    }
                }
            }
        }
    }
}
