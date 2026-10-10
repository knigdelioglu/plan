package com.plan.app.core.design.components.extended

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.graphics.SolidColor
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
 * Real selection-aware teacher note editor. Markdown is stored as plain text;
 * formatting only wraps selected text or inserts syntax at the caret.
 */
@Composable
fun ArcRichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = ArcTheme.colors
    val inputFocus = remember { FocusRequester() }
    var field by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }
    LaunchedEffect(value) {
        if (field.text != value) {
            field = TextFieldValue(value, selection = TextRange(value.length))
        }
    }

    fun apply(format: NoteFormat) {
        val next = formatTeacherNote(
            field.text, field.selection.start, field.selection.end, format
        )
        field = TextFieldValue(
            text = next.text,
            selection = TextRange(next.selectionStart, next.selectionEnd)
        )
        onValueChange(next.text)
        inputFocus.requestFocus()
    }

    ArcCard(modifier = modifier) {
        Column(modifier = Modifier.padding(ArcSpacing.space3)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Triple(Icons.Default.FormatBold, "Kalın", NoteFormat.BOLD),
                    Triple(Icons.Default.FormatItalic, "İtalik", NoteFormat.ITALIC),
                    Triple(Icons.Default.FormatListBulleted, "Liste", NoteFormat.LIST),
                    Triple(Icons.Default.FormatQuote, "Alıntı", NoteFormat.QUOTE)
                ).forEach { (icon, label, format) ->
                    IconButton(
                        onClick = { apply(format) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(icon, contentDescription = label, tint = colors.foreground)
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ArcRadius.md,
                color = colors.surface,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    BasicTextField(
                        value = field,
                        onValueChange = {
                            field = it
                            if (it.text != value) onValueChange(it.text)
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 132.dp)
                            .focusRequester(inputFocus)
                            .testTag("teacher-note-input"),
                        textStyle = ArcTheme.typography.bodyMedium.copy(color = colors.foreground),
                        cursorBrush = SolidColor(colors.accent),
                        decorationBox = { inner ->
                            Box {
                                if (field.text.isEmpty()) {
                                    Text(
                                        "Öğretmen notunuzu yazın...",
                                        style = ArcTheme.typography.bodyMedium,
                                        color = colors.textMuted
                                    )
                                }
                                inner()
                            }
                        }
                    )
                    Text(
                        text = "${value.length} karakter",
                        style = ArcTheme.typography.labelSmall,
                        color = colors.textMuted
                    )
                }
            }
        }
    }
}
