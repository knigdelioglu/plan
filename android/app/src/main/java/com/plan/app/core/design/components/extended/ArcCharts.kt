package com.plan.app.core.design.components.extended

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme

/**
 * Arc Line Chart Component (64 / 70 - C.9)
 * Canvas-rendered line chart showing cumulative hours / progress trajectory across 37 weeks.
 */
@Composable
fun ArcLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = ArcTheme.colors.accent,
    height: Dp = 160.dp
) {
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Text(
                text = "Haftalık Kümülatif İlerleme Eğrisi",
                style = ArcTheme.typography.titleMedium,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(ArcSpacing.space2))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                if (dataPoints.size < 2) return@Canvas
                val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
                val stepX = size.width / (dataPoints.size - 1)

                val path = Path()
                dataPoints.forEachIndexed { i, pt ->
                    val x = i * stepX
                    val y = size.height - (pt / maxVal * (size.height - 20.dp.toPx())) - 10.dp.toPx()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                // Baseline
                drawLine(
                    color = colors.border,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )

                // Chart line
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw end point dot
                val lastX = size.width
                val lastY = size.height - (dataPoints.last() / maxVal * (size.height - 20.dp.toPx())) - 10.dp.toPx()
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = Offset(lastX, lastY))
            }
        }
    }
}

/**
 * Arc Donut Chart Component (65 / 70 - C.10)
 * Canvas-rendered donut chart showing theme hour distribution.
 */
data class DonutSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun ArcDonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    centerTitle: String = "185 Saat",
    centerSubtitle: String = "4 Tema",
    chartSize: Dp = 180.dp
) {
    val colors = ArcTheme.colors
    val total = slices.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    ArcCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(ArcSpacing.space4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tema Ders Saati Dağılımı",
                style = ArcTheme.typography.titleMedium,
                color = colors.foreground
            )
            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            Box(
                modifier = Modifier.size(chartSize),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    val strokeWidth = 24.dp.toPx()

                    slices.forEach { slice ->
                        val sweepAngle = (slice.value / total) * 360f
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 2f, // small gap
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        )
                        startAngle += sweepAngle
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = centerTitle,
                        style = ArcTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.foreground
                    )
                    Text(
                        text = centerSubtitle,
                        style = ArcTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            // Legend
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                slices.forEach { slice ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(1.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(color = slice.color)
                                }
                            }
                            Text(text = slice.label, style = ArcTheme.typography.bodySmall, color = colors.foreground)
                        }
                        Text(
                            text = "${slice.value.toInt()} sa",
                            style = ArcTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Arc Usage Meter Component (66 / 70 - C.11)
 * Gauge/meter showing completed hours vs 180 verified / 185 assumed.
 */
@Composable
fun ArcUsageMeter(
    completedHours: Int,
    verifiedHours: Int? = null,
    assumedHours: Int? = null,
    annualHours: Int,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    val percentage = if (annualHours > 0) ((completedHours.toFloat() / annualHours) * 100).toInt() else 0

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Yıllık Müfredat Tamamlanma", style = ArcTheme.typography.titleMedium, color = colors.foreground)
                Text(text = "%$percentage", style = ArcTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.accent)
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space2))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            ) {
                // Background track
                drawRoundRect(
                    color = colors.surfaceMuted,
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
                )

                // Verified marker is only shown when explicitly present in source.
                if (verifiedHours != null && annualHours > 0) {
                    val verifiedX = (verifiedHours.toFloat() / annualHours) * size.width
                    drawLine(
                        color = colors.borderStrong,
                        start = Offset(verifiedX, 0f),
                        end = Offset(verifiedX, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Fill progress
                val fillWidth = if (annualHours > 0) (completedHours.toFloat() / annualHours).coerceIn(0f, 1f) * size.width else 0f
                if (fillWidth > 0f) {
                    drawRoundRect(
                        color = colors.accent,
                        size = Size(fillWidth, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$completedHours / $annualHours saat işlendi", style = ArcTheme.typography.bodySmall, color = colors.textSecondary)
                Text(
                    text = if (verifiedHours != null) "Doğrulanmış: $verifiedHours sa" +
                        (assumedHours?.takeIf { it > 0 }?.let { " (+$it varsayımsal)" } ?: "")
                    else "Kaynakta doğrulama ayrımı belirtilmemiş",
                    style = ArcTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = colors.textMuted
                )
            }
        }
    }
}

/**
 * Arc Bar Chart Component (69 / 70 - C.14)
 * Canvas-rendered bar chart for hours per theme / outcomes.
 */
data class BarChartItem(
    val label: String,
    val value: Float,
    val color: Color? = null
)

@Composable
fun ArcBarChart(
    items: List<BarChartItem>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 140.dp
) {
    val colors = ArcTheme.colors
    val maxVal = (items.maxOfOrNull { it.value } ?: 1f).coerceAtLeast(1f)

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space4)) {
            Text(text = "Tema Başına Ders Saati Dağılımı", style = ArcTheme.typography.titleMedium, color = colors.foreground)
            Spacer(modifier = Modifier.height(ArcSpacing.space3))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
            ) {
                val barWidth = 36.dp.toPx()
                val totalBars = items.size
                val space = (size.width - (totalBars * barWidth)) / (totalBars + 1)

                items.forEachIndexed { i, it ->
                    val x = space + i * (barWidth + space)
                    val barH = (it.value / maxVal) * (size.height - 24.dp.toPx())
                    val y = size.height - barH - 4.dp.toPx()

                    drawRoundRect(
                        color = it.color ?: colors.accent,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items.forEach {
                    Text(text = it.label, style = ArcTheme.typography.labelSmall, color = colors.textSecondary)
                }
            }
        }
    }
}
