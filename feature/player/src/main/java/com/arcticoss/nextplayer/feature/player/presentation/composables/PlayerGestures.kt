package com.arcticoss.nextplayer.feature.player.presentation.composables

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.PlayerEvent
import com.arcticoss.nextplayer.feature.player.UiEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SeekParameters
import kotlinx.coroutines.delay
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
    val focusRequester = remember { FocusRequester() }
    var volumeKeyEvent by remember { mutableStateOf(false) }
    var whichBar by remember { mutableStateOf(Bar.None) }

    val SCROLL_STEP = LocalDensity.current.run { 16.dp.toPx() }
    val SCROLL_STEP_SEEK = LocalDensity.current.run { 8.dp.toPx() }
    val SEEK_STEP = 1000

    LaunchedEffect(whichBar) {
        when(whichBar) {
            Bar.Brightness -> onUiEvent(UiEvent.ShowBrightnessBar(true))
            Bar.Volume -> onUiEvent(UiEvent.ShowVolumeBar(true))
            Bar.None -> {
                delay(1000)
                onUiEvent(UiEvent.ShowBrightnessBar(false))
                onUiEvent(UiEvent.ShowVolumeBar(false))
            }
        }
    }

    LaunchedEffect(volumeKeyEvent) {
        if (volumeKeyEvent) {
            onUiEvent(UiEvent.ShowVolumeBar(true))
            delay(2000)
            onUiEvent(UiEvent.ShowVolumeBar(false))
            volumeKeyEvent = false
        }
    }

    Box(
        modifier = Modifier
            .systemGesturesPadding()
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_UP
                    && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                ) {
                    volumeKeyEvent = true
                    onEvent(PlayerEvent.IncreaseVolume)
                    return@onKeyEvent true
                }
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                    && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                ) {
                    volumeKeyEvent = true
                    onEvent(PlayerEvent.DecreaseVolume)
                    return@onKeyEvent true
                }
                false
            }
            .focusRequester(focusRequester)
            .focusable()
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
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val width = context.resources.displayMetrics.widthPixels
                        gestureScrollY = offset.y

                        whichBar = if (offset.x < (width / 2)) {
                            Bar.Brightness
                        } else {
                            Bar.Volume
                        }
                    },
                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->

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
                                Bar.None -> {}
                            }
                            gestureScrollY = change.position.y
                        }

                    },
                    onDragEnd = {
                        whichBar = Bar.None
                    }
                )
            }
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

fun Density.toDp(px: Float): Float {
    return px / this.density
}

enum class Bar {
    Brightness, Volume, None
}
