package com.plan.app.core.design.components.extended

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.plan.app.core.design.motion.ArcMotionTokens
import com.plan.app.core.design.theme.ArcTheme
import kotlinx.coroutines.delay

/**
 * Arc Animated Counter Component (60 / 70 - C.5)
 * Rolling number counter for lesson hours, weeks, completion %.
 */
@Composable
fun ArcAnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = ArcTheme.typography.displayMedium,
    color: Color = ArcTheme.colors.foreground,
    suffix: String = ""
) {
    val animatedCount by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = ArcMotionTokens.Duration.Considered),
        label = "animated_counter"
    )

    AnimatedContent(
        targetState = animatedCount,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
            } else {
                slideInVertically { height -> -height } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
            }
        },
        label = "animated_counter_content",
        modifier = modifier
    ) { count ->
        Text(
            text = "$count$suffix",
            style = style,
            color = color
        )
    }
}

/**
 * Arc Text Morph Component (61 / 70 - C.6)
 * Smooth morph between two text strings.
 */
@Composable
fun ArcTextMorph(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = ArcTheme.typography.titleMedium,
    color: Color = ArcTheme.colors.foreground
) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "text_morph",
        modifier = modifier
    ) { targetText ->
        Text(
            text = targetText,
            style = style,
            color = color
        )
    }
}

/**
 * Arc Text Reveal Component (62 / 70 - C.7)
 * Staggered word/character reveal animation for titles.
 */
@Composable
fun ArcTextReveal(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = ArcTheme.typography.titleLarge,
    color: Color = ArcTheme.colors.foreground,
    delayPerWordMillis: Long = 40L
) {
    val words = remember(text) { text.split(" ") }
    var visibleWordCount by remember(text) { mutableIntStateOf(0) }

    LaunchedEffect(text) {
        visibleWordCount = 0
        for (i in 1..words.size) {
            delay(delayPerWordMillis)
            visibleWordCount = i
        }
    }

    Row(modifier = modifier) {
        val revealedText = words.take(visibleWordCount).joinToString(" ")
        Text(
            text = revealedText,
            style = style,
            color = color
        )
    }
}
