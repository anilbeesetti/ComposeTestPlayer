package com.arcticoss.feature.player.presentation.composables

import android.util.Log
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.arcticoss.feature.player.Bar
import com.arcticoss.feature.player.PlayerEvent
import com.arcticoss.feature.player.UiEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


private const val TAG = "PlayerGestures"

@Composable
fun PlayerGestures(
    player: ExoPlayer,
    onUiEvent: (UiEvent) -> Unit,
    onEvent: (PlayerEvent) -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val SCROLL_STEP = LocalDensity.current.run { 16.dp.toPx() }
    val SCROLL_STEP_SEEK = LocalDensity.current.run { 8.dp.toPx() }
    val IGNORE_BORDER = LocalDensity.current.run { 24.dp.toPx() }
    val SEEK_STEP = 1000

    Box(
        modifier = Modifier
            .systemGesturesPadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onUiEvent(UiEvent.ToggleShowUi) },
                    onDoubleTap = {
                        if (player.playWhenReady) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                var gestureScrollX = 0f
                var playerCurrentState = false
                var seekChange = 0L
                var seekStart = 0L
                var seekMax = 0L
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        playerCurrentState = player.playWhenReady
                        player.playWhenReady = false
                        gestureScrollX = offset.x
                        seekChange = 0L
                        seekStart = player.currentPosition
                        seekMax = player.duration
                        onUiEvent(UiEvent.ShowSeekBar(true))

                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        change.consume()
                        val offset = gestureScrollX - change.position.x
                        val position: Long
                        if (abs(offset) > SCROLL_STEP_SEEK) {
                            val distanceDiff =
                                max(0.5f, min(abs(density.toDp(offset) / 4), 10.0f))
                            if (offset > 0) {
                                if (seekStart + seekChange - SEEK_STEP * distanceDiff >= 0) {
                                    player.setSeekParameters(SeekParameters.PREVIOUS_SYNC)
                                    seekChange -= SEEK_STEP * distanceDiff.toLong()
                                    position = seekStart + seekChange
                                    player.seekTo(position)
                                }
                            } else {
                                if (seekMax == C.TIME_UNSET || seekStart + seekChange + SEEK_STEP * distanceDiff < seekMax) {
                                    player.setSeekParameters(SeekParameters.NEXT_SYNC)
                                    seekChange += SEEK_STEP * distanceDiff.toLong()
                                    position = seekStart + seekChange
                                    player.seekTo(position)
                                }
                            }
                            Log.d(TAG, "PlayerScreen: $seekChange")
                            gestureScrollX = change.position.x
                        }

                    },
                    onDragCancel = {
                        onUiEvent(UiEvent.ShowSeekBar(false))
                    },
                    onDragEnd = {
                        player.playWhenReady = playerCurrentState
                        onUiEvent(UiEvent.ShowSeekBar(false))
                    }
                )
            }
            .pointerInput(Unit) {
                var gestureScrollY = 0.0f
                var whichBar = Bar.Brightness
                var startAtIgnoreBorder = false
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val width = context.resources.displayMetrics.widthPixels
                        val height = context.resources.displayMetrics.heightPixels
                        gestureScrollY = offset.y
                        if (offset.y < IGNORE_BORDER || offset.y > height - IGNORE_BORDER) {
                            startAtIgnoreBorder = true
                        } else {
                            startAtIgnoreBorder = false
                            if (offset.x < (width / 2)) {
                                whichBar = Bar.Brightness
                                onUiEvent(UiEvent.ShowBrightnessBar(true))
                            } else {
                                whichBar = Bar.Volume
                                onUiEvent(UiEvent.ShowVolumeBar(true))
                            }
                        }
                    },
                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->

                        if (!startAtIgnoreBorder) {
                            change.consume()
                            val offset = gestureScrollY - change.position.y

                            if (abs(offset * dragAmount) > SCROLL_STEP) {
                                when (whichBar) {
                                    Bar.Volume -> {
                                        if (offset > 0) {
                                            onEvent(PlayerEvent.IncreaseVolume)
                                        } else {
                                            onEvent(PlayerEvent.DecreaseVolume)
                                        }
                                    }
                                    Bar.Brightness -> {
                                        if (offset > 0) {
                                            onEvent(PlayerEvent.IncreaseBrightness)
                                        } else {
                                            onEvent(PlayerEvent.DecreaseBrightness)
                                        }
                                    }
                                }
                                gestureScrollY = change.position.y
                            }
                        }
                    },
                    onDragEnd = {
                        onUiEvent(UiEvent.ShowBrightnessBar(false))
                        onUiEvent(UiEvent.ShowVolumeBar(false))
                    },
                    onDragCancel = {
                        onUiEvent(UiEvent.ShowBrightnessBar(false))
                        onUiEvent(UiEvent.ShowVolumeBar(false))
                    }
                )
            }
    )
}

fun Density.toDp(px: Float): Float {
    return px / this.density
}

