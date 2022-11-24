package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.presentation.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.presentation.MediaState
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.google.android.exoplayer2.SeekParameters
import java.util.concurrent.TimeUnit


private const val TAG = "MediaGestures"

@Composable
fun MediaGestures(
    mediaState: MediaState,
    controller: ControllerState,
) {

    Box(
        modifier = Modifier
            .systemGesturesPadding()
            .fillMaxSize()
            .pointerInput(controller) {
                detectTapGestures(
                    onTap = {
                        mediaState.controllerVisibility = when (mediaState.controllerVisibility) {
                            ControllerVisibility.Visible -> ControllerVisibility.Invisible
                            ControllerVisibility.PartiallyVisible -> ControllerVisibility.Visible
                            ControllerVisibility.Invisible -> ControllerVisibility.Visible
                        }
                    },
                    onDoubleTap = { controller.playOrPause() }
                )
            }
            .pointerInput(Unit) {
                var totalOffset = Offset.Zero
                var wasPlaying = false
                var diffTime = 0f

                var currentPosition = 0L
                var duration = 0L
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        wasPlaying = controller.isPlaying
                        controller.pause()
                        totalOffset = Offset.Zero

                        currentPosition = controller.positionMs
                        duration = controller.durationMs

                        // show seek bar
                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        val previousOffset = totalOffset
                        totalOffset += Offset(x = dragAmount, y = 0f)

                        val finalTime = currentPosition + (totalOffset.x * 100)

                        if (finalTime >= 0 && finalTime < duration) {
                            if (previousOffset.x < totalOffset.x) {
                                controller.setSeekParameters(SeekParameters.NEXT_SYNC)
                            } else {
                                controller.setSeekParameters(SeekParameters.PREVIOUS_SYNC)
                            }
                            diffTime = finalTime - currentPosition
                            controller.seekTo(finalTime.toLong())
                        }
                        // show time diff
                        change.consume()
                    },
                    onDragEnd = {
                        if (wasPlaying) controller.play()
                        // hide seek bar
                    }
                )
            }
    )
}