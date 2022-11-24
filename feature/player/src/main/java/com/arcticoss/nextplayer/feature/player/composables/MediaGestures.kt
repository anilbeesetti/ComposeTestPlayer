package com.arcticoss.nextplayer.feature.player.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.presentation.ControllerBar
import com.arcticoss.nextplayer.feature.player.presentation.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.presentation.MediaState
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.arcticoss.nextplayer.feature.player.utils.decreaseVolume
import com.arcticoss.nextplayer.feature.player.utils.increaseVolume
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs


private const val TAG = "MediaGestures"

@Composable
fun MediaGestures(
    mediaState: MediaState,
    controller: ControllerState,
) {

    val context = LocalContext.current
    val SCROLL_STEP = LocalDensity.current.run { 16.dp.toPx() }
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

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
                var isControllerShowing = mediaState.isControllerShowing
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

                        isControllerShowing = mediaState.isControllerShowing
                        if (!isControllerShowing)
                            mediaState.controllerVisibility = ControllerVisibility.PartiallyVisible
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
                        if (!isControllerShowing)
                            mediaState.controllerVisibility = ControllerVisibility.Invisible
                    }
                )
            }
            .pointerInput(Unit) {
                var gestureScrollY = 0f
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val width = context.resources.displayMetrics.widthPixels
                        gestureScrollY = offset.y

                        if (offset.x < (width / 2)) {
                            mediaState.controllerBar = ControllerBar.Brightness
                        } else {
                            mediaState.controllerBar = ControllerBar.Volume
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->

                        val offset = gestureScrollY - change.position.y

                        if (abs(offset * dragAmount) > SCROLL_STEP) {
                            when (mediaState.controllerBar) {
                                ControllerBar.Volume -> {
                                    if (offset > 0) {
                                        audioManager.increaseVolume()
                                    } else {
                                        audioManager.decreaseVolume()
                                    }
                                }
                                ControllerBar.Brightness -> {
                                    if (offset > 0) {
                                        // Increase brightness
                                    } else {
                                        // Decrease brightness
                                    }
                                }
                                ControllerBar.None -> {/* Do Nothing */}
                            }
                            gestureScrollY = change.position.y
                            change.consume()
                        }
                    },
                    onDragEnd = {
                        mediaState.controllerBar = ControllerBar.None
                    }
                )
            }
    )
}