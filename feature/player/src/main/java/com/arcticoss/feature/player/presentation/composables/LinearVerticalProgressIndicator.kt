package com.arcticoss.feature.player.presentation.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

internal val LinearIndicatorWidth = 4.dp
internal val LinearIndicatorHeight = 240.dp

@Composable
fun LinearVerticalProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
) {
    Canvas(
        modifier
            .progressSemantics(progress)
            .size(LinearIndicatorWidth, LinearIndicatorHeight)
    ) {
        val strokeWidth = size.width
        drawLinearIndicatorTrack(trackColor, strokeWidth)
        drawLinearIndicator(0f, progress, color, strokeWidth)
    }
}

private fun DrawScope.drawLinearIndicatorTrack(
    color: Color,
    strokeWidth: Float
) = drawLinearIndicator(0f, 1f, color, strokeWidth)


private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float
) {
    val width = size.width
    val height = size.height
    // Start drawing from the vertical center of the stroke
    val xOffset = width / 2

    val barStart = (1 - startFraction) * height
    val barEnd = (1 - endFraction) * height

    // Progress line
    if (endFraction > 0) {
        drawLine(
            color,
            Offset(xOffset, barStart),
            Offset(xOffset, barEnd),
            strokeWidth,
            cap = StrokeCap.Round
        )
    }
}