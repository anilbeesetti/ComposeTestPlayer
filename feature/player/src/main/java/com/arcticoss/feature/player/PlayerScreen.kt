package com.arcticoss.feature.player

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.presentation.composables.NextExoPlayer
import com.arcticoss.feature.player.presentation.composables.NextPlayerUI
import com.arcticoss.feature.player.utils.Utils
import com.arcticoss.feature.player.utils.findActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


private const val TAG = "NextPlayerScreen"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val player = viewModel.player
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()

    val SCROLL_STEP = Utils.dpToPx(16)
    val SCROLL_STEP_SEEK = Utils.dpToPx(8)
    val SEEK_STEP = 1000

    Box(
        modifier = Modifier
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_UP
                    && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                ) {
                    viewModel.increaseVolume()
                    return@onKeyEvent true
                }
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                    && it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN
                ) {
                    viewModel.decreaseVolume()
                    return@onKeyEvent true
                }
                false
            }
            .focusRequester(focusRequester)
            .focusable()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        viewModel.onUiEvent(PlayerUiEvent.ShowUi(!playerUiState.showUi))
                    },
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
                    onDragStart = {
                        playerCurrentState = player.playWhenReady
                        player.playWhenReady = false
                        gestureScrollX = it.x
                        seekChange = 0L
                        seekStart = player.currentPosition
                        seekMax = player.duration
                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        val offset = gestureScrollX - change.position.x
                        val position: Long
                        if (abs(offset) > SCROLL_STEP_SEEK) {
                            val distanceDiff = max(0.5f, min(abs(Utils.pxToDp(offset) / 4), 10.0f))
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
                            gestureScrollX = change.position.x
                        }
                    },
                    onDragEnd = {
                        player.playWhenReady = playerCurrentState
                    }
                )
            }
            .pointerInput(Unit) {
                val activity = context.findActivity()
                val width = activity?.resources?.displayMetrics?.widthPixels ?: 0
                val height = activity?.resources?.displayMetrics?.heightPixels ?: 0
                var gestureScrollY = 0.0f
                var whichBar = Bar.Brightness
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        gestureScrollY = offset.y
                        whichBar = (if (offset.x < (width / 2)) Bar.Brightness else Bar.Volume)
                    },
                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->
                        change.consume()
                        val offset = gestureScrollY - change.position.y

                        if (abs(offset * dragAmount) > SCROLL_STEP) {
                            when (whichBar) {
                                Bar.Volume -> {
                                    if (offset > 0) {
                                        viewModel.increaseVolume()
                                    } else {
                                        viewModel.decreaseVolume()
                                    }
                                }
                                Bar.Brightness -> {
                                    if (offset > 0) {
                                        viewModel.increaseBrightness()
                                    } else {
                                        viewModel.decreaseBrightness()
                                    }
                                }
                            }
                            gestureScrollY = change.position.y
                        }
                    }
                )
            }
    ) {
        NextExoPlayer(
            onBackPressed = {},
            changeOrientation = { requestedOrientation ->
                val activity = context.findActivity()
                activity?.requestedOrientation = requestedOrientation
                activity?.requestedOrientation?.let {
                    viewModel.onEvent(
                        PlayerEvent.ChangeOrientation(
                            it
                        )
                    )
                }
            }
        )
        NextPlayerUI(
            onBackPressed = {}
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


