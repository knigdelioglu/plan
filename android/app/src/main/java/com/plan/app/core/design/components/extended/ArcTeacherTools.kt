package com.plan.app.core.design.components.extended

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plan.app.core.design.components.advanced.ArcTextarea
import com.plan.app.core.design.components.core.ArcButton
import com.plan.app.core.design.components.core.ArcButtonVariant
import com.plan.app.core.design.components.core.ArcCard
import com.plan.app.core.design.theme.ArcRadius
import com.plan.app.core.design.theme.ArcSpacing
import com.plan.app.core.design.theme.ArcTheme
import com.plan.app.core.model.DrawingPoint
import com.plan.app.core.model.DrawingStroke

/**
 * Arc Signature Pad Component (67 / 70 - C.12)
 * Stylus / touch drawing canvas for teacher signature & handwritten weekly annotations.
 */
@Composable
fun ArcSignaturePad(
    initialStrokes: List<DrawingStroke> = emptyList(),
    onStrokesChanged: (List<DrawingStroke>) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    val strokes = remember { mutableStateListOf<DrawingStroke>().apply { addAll(initialStrokes) } }
    var currentPoints by remember { mutableStateOf<List<DrawingPoint>>(emptyList()) }
    var selectedColor by remember { mutableStateOf("#18181B") }
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space3)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "El Yazısı & İmza Alanı (Kalem Destekli)",
                    style = ArcTheme.typography.titleMedium,
                    color = colors.foreground
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("#18181B", "#2563EB", "#DC2626").forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(hex)))
                                .clickable { selectedColor = hex }
                        )
                    }

                    IconButton(
                        onClick = {
                            strokes.clear()
                            currentPoints = emptyList()
                            onStrokesChanged(emptyList())
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Temizle", tint = colors.danger)
                    }
                }
            }

            Spacer(modifier = Modifier.height(ArcSpacing.space2))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                shape = ArcRadius.md,
                color = colors.surfaceMuted,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints = listOf(DrawingPoint(offset.x, offset.y))
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints = currentPoints + DrawingPoint(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) {
                                        val newStroke = DrawingStroke(
                                            points = currentPoints,
                                            colorHex = selectedColor,
                                            strokeWidth = 4f
                                        )
                                        strokes.add(newStroke)
                                        currentPoints = emptyList()
                                        onStrokesChanged(strokes.toList())
                                    }
                                }
                            )
                        }
                ) {
                    // Draw saved strokes
                    strokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path()
                            path.moveTo(stroke.points[0].x, stroke.points[0].y)
                            for (i in 1 until stroke.points.size) {
                                path.lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                            drawPath(
                                path = path,
                                color = Color(android.graphics.Color.parseColor(stroke.colorHex)),
                                style = Stroke(
                                    width = stroke.strokeWidth.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Draw active dragging stroke
                    if (currentPoints.size > 1) {
                        val activePath = Path()
                        activePath.moveTo(currentPoints[0].x, currentPoints[0].y)
                        for (i in 1 until currentPoints.size) {
                            activePath.lineTo(currentPoints[i].x, currentPoints[i].y)
                        }
                        drawPath(
                            path = activePath,
                            color = Color(android.graphics.Color.parseColor(selectedColor)),
                            style = Stroke(
                                width = 4f.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Arc Rich Text Editor Component (68 / 70 - C.13)
 * Formatted teacher notes toolbar (Markdown support: bold, italic, bullet list, quote).
 */
@Composable
fun ArcRichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space3)) {
            // Formatting Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onValueChange("$value **vurgu**") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Kalın", tint = colors.foreground)
                }
                IconButton(
                    onClick = { onValueChange("$value *italik*") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatItalic, contentDescription = "İtalik", tint = colors.foreground)
                }
                IconButton(
                    onClick = { onValueChange("$value\n• Madde") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Liste", tint = colors.foreground)
                }
                IconButton(
                    onClick = { onValueChange("$value\n> Alıntı") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatQuote, contentDescription = "Alıntı", tint = colors.foreground)
                }
            }

            ArcTextarea(
                value = value,
                onValueChange = onValueChange,
                placeholder = "Biçimlendirilmiş öğretmen ders notları...",
                minLines = 5
            )
        }
    }
}
