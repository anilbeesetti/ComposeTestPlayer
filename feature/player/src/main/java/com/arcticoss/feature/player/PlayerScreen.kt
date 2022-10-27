package com.arcticoss.feature.player

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.presentation.composables.EventHandler
import com.arcticoss.feature.player.presentation.composables.NextExoPlayer
import com.arcticoss.feature.player.presentation.composables.NextPlayerUI
import com.arcticoss.feature.player.utils.findActivity
import com.arcticoss.model.PlayerPreferences
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


private const val TAG = "NextPlayerScreen"


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayerScreen(
    onBackPressed: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val player = viewModel.player
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()
    val playerCurrentPosition by viewModel.playerCurrentPosition.collectAsStateWithLifecycle()

    EventHandler(
        playerState = playerState,
        playerUiState = playerUiState,
        onEvent = viewModel::onEvent,
        onUiEvent = viewModel::onUiEvent
    )
    PlayerScreen(
        player = player,
        playerState = playerState,
        playerUiState = playerUiState,
        currentPosition = playerCurrentPosition,
        preferences = preferences,
        onEvent = viewModel::onEvent,
        onUiEvent = viewModel::onUiEvent,
        onBackPressed = onBackPressed
    )
}


@Composable
internal fun PlayerScreen(
    player: ExoPlayer,
    currentPosition: Long,
    playerState: PlayerState,
    playerUiState: PlayerUiState,
    preferences: PlayerPreferences,
    onEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    onBackPressed: () -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val density = LocalDensity.current
    val activity = remember { context.findActivity() }

    val width = activity?.resources?.displayMetrics?.widthPixels ?: 0
    val height = activity?.resources?.displayMetrics?.heightPixels ?: 0

    val SCROLL_STEP = LocalDensity.current.run { 16.dp.toPx() }
    val SCROLL_STEP_SEEK = LocalDensity.current.run { 8.dp.toPx() }
    val IGNORE_BORDER = LocalDensity.current.run { 24.dp.toPx() }
    val SEEK_STEP = 1000


    Box(
        modifier = Modifier
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_UP
                    && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                ) {
                    onEvent(PlayerEvent.IncreaseVolume)
                    return@onKeyEvent true
                }
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                    && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                ) {
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
                var startAtIgnoreBorder = false
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        if (offset.x < IGNORE_BORDER || offset.y < IGNORE_BORDER || offset.x > width - IGNORE_BORDER || offset.y > height - IGNORE_BORDER) {
                            startAtIgnoreBorder = true
                        } else {
                            startAtIgnoreBorder = false
                            playerCurrentState = player.playWhenReady
                            player.playWhenReady = false
                            gestureScrollX = offset.x
                            seekChange = 0L
                            seekStart = player.currentPosition
                            seekMax = player.duration
                            onUiEvent(UiEvent.ShowSeekBar(true))
                        }
                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        if (!startAtIgnoreBorder) {
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
                                        player.setSeekParameters(SeekParameters.PREVIOUS_SYNC)
                                        seekChange += SEEK_STEP * distanceDiff.toLong()
                                        position = seekStart + seekChange
                                        player.seekTo(position)
                                    }
                                }
                                Log.d(TAG, "PlayerScreen: $seekChange")
                                gestureScrollX = change.position.x
                            }
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
    ) {
        NextExoPlayer(
            exoPlayer = player,
            playWhenReady = playerState.playWhenReady,
            aspectRatio = preferences.aspectRatio,
            onBackPressed = onBackPressed,
            onEvent = onEvent
        )
        NextPlayerUI(
            player = player,
            playerState = playerState,
            playerUiState = playerUiState,
            currentPosition = currentPosition,
            preferences = preferences,
            onBackPressed = onBackPressed,
            onUiEvent = onUiEvent
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

fun Density.toDp(px: Float): Float {
    return px / this.density
}
