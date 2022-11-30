package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.arcticoss.nextplayer.feature.player.state.MediaState

@Composable
fun Subtitle(
    mediaState: MediaState,
    modifier: Modifier = Modifier
) {
    var offsetY by remember { mutableStateOf(-20f) }
    var scale by remember { mutableStateOf(1f) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
    }

    mediaState.playerState?.let {
        Column(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationY = offsetY
                }
                .then(
                    if (mediaState.isControllerLocked)
                        Modifier
                    else
                        Modifier
                            .draggable(
                                orientation = Orientation.Vertical,
                                state = rememberDraggableState { delta ->
                                    if (offsetY + delta < 0) {
                                        offsetY += delta
                                    }
                                }
                            )
                            .transformable(state = state)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            it.cueGroup.cues.forEach {
                Text(
                    text = it.text.toString(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(5.0f, 5.0f)
                        )
                    )
                )
            }
        }
    }
}