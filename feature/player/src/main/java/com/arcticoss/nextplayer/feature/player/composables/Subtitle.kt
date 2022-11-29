package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.android.exoplayer2.text.CueGroup
import kotlin.math.roundToInt

@Composable
fun Subtitle(
    cueGroup: CueGroup,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .onSizeChanged { size = it }
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .offset { IntOffset(0, offsetY.roundToInt()) }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        if (offsetY + delta < 0) {
                            offsetY += delta
                        }
                    }
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cueGroup.cues.forEach {
                Text(
                    text = it.text.toString(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}