package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.presentation.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.presentation.MediaState
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


private const val TAG = "MediaGestures"

@Composable
fun MediaGestures(
    mediaState: MediaState,
    controller: ControllerState
) {

    val SCROLL_STEP_SEEK = LocalDensity.current.run { 8.dp.toPx() }
    val SEEK_STEP = 1000
    val density = LocalDensity.current

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
                var gestureScrollX = 0f
                var wasPlaying = false
                var seekChange = 0L

                var currentPosition = 0L
                var duration = 0L
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        wasPlaying = controller.isPlaying
                        controller.pause()
                        gestureScrollX = offset.x

                        currentPosition = controller.positionMs
                        duration = controller.durationMs
                        // show seek bar
                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        change.consume()
                        val offset = gestureScrollX - change.position.x
                        val position: Long
                        if (abs(offset) > SCROLL_STEP_SEEK) {
                            val distanceDiff =
                                max(0.5f, min(abs(density.toDp(offset) / 4), 10.0f))
                            if (offset > 0) {
                                if (currentPosition + seekChange - SEEK_STEP * distanceDiff >= 0) {
                                    controller.setSeekParameters(SeekParameters.PREVIOUS_SYNC)
                                    seekChange -= SEEK_STEP * distanceDiff.toLong()
                                    position = currentPosition + seekChange
                                    controller.seekTo(position)
                                }
                            } else {
                                if (duration == C.TIME_UNSET || currentPosition + seekChange + SEEK_STEP * distanceDiff < duration) {
                                    controller.setSeekParameters(SeekParameters.NEXT_SYNC)
                                    seekChange += SEEK_STEP * distanceDiff.toLong()
                                    position = currentPosition + seekChange
                                    controller.seekTo(position)
                                }
                            }
                            gestureScrollX = change.position.x
                        }
                    },
                    onDragEnd = {
                        if (wasPlaying) controller.play()
                    }
                )
            }
    )
}

fun Density.toDp(px: Float): Float {
    return px / this.density
}