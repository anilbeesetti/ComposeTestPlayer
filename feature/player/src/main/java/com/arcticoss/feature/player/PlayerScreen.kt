package com.arcticoss.feature.player

import android.util.Log
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
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.presentation.composables.NextExoPlayer
import com.arcticoss.feature.player.presentation.composables.NextPlayerUI
import com.arcticoss.feature.player.utils.findActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs


private const val TAG = "NextPlayerScreen"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val player = viewModel.player as ExoPlayer
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .onKeyEvent {
                Log.d(TAG, "PlayerScreen: ${it.key.nativeKeyCode}")
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
                var dragStartOffset = 0f
                var playerCurrentState = false
                detectHorizontalDragGestures(
                    onDragStart = {
                        playerCurrentState = player.playWhenReady
                        player.playWhenReady = false
                        dragStartOffset = it.x
                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        val seekAmount = abs(change.position.x - dragStartOffset) * dragAmount
                        val newPosition = (playerState.currentPosition + seekAmount.toLong())
                            .coerceIn(0..playerState.currentMediaItemDuration)
                        viewModel.seekTo(newPosition)
                        dragStartOffset = change.position.x
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
                var initialOffset = 0.0f
                var whichBar = Bar.Brightness
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        initialOffset = offset.y
                        whichBar = (if (offset.x < (width / 2)) Bar.Brightness else Bar.Volume)
                    },
                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->
                        val offset = change.position.y - initialOffset
                        val isOffsetEnough = abs(offset) > height / 40
                        val isDragEnough = abs(dragAmount) > 100
                        if (isOffsetEnough || isDragEnough) {
                            val yChange = change.position.y - initialOffset
                            when (whichBar) {
                                Bar.Volume -> {
                                    if (yChange < 0) {
                                        viewModel.increaseVolume()
                                    } else {
                                        viewModel.decreaseVolume()
                                    }
                                }
                                Bar.Brightness -> {
                                    if (yChange < 0) {
                                        viewModel.increaseBrightness()
                                    } else {
                                        viewModel.decreaseBrightness()
                                    }
                                }
                            }
                            initialOffset = change.position.y
                        }
                    }
                )
            }
    ) {
        NextExoPlayer(
            onBackPressed = onBackPressed,
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
            onBackPressed = onBackPressed
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


