package com.arcticoss.feature.player

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.util.Log
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.presentation.composables.NextExoPlayer
import com.arcticoss.feature.player.presentation.composables.NextPlayerUI
import com.arcticoss.feature.player.utils.*
import com.google.android.exoplayer2.ExoPlayer
import kotlin.math.abs


private const val TAG = "NextPlayerScreen"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val player = viewModel.player as ExoPlayer
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()

    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    Box(
        modifier = Modifier
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
                        player.seekTo(newPosition)
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
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        initialOffset = offset.y
                        if (offset.x < (width / 2)) {
                            viewModel.onUiEvent(PlayerUiEvent.ShowBrightnessBar(true))
                        } else {
                            viewModel.onUiEvent(PlayerUiEvent.ShowVolumeBar(true))
                        }
                    },
                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->
                        val offset = change.position.y - initialOffset
                        val isOffsetEnough = abs(offset) > height / 40
                        val isDragEnough = abs(dragAmount) > 100
                        if (isOffsetEnough || isDragEnough) {
                            if (playerUiState.showVolumeBar) {
                                if (change.position.y - initialOffset < 0) {
                                    viewModel.increaseVolume()
                                } else {
                                    viewModel.decreaseVolume()
                                }
                            }
                            if (playerUiState.showBrightnessBar) {
                                if (change.position.y - initialOffset < 0) {
                                    viewModel.onEvent(PlayerEvent.IncreaseBrightness)
                                } else {
                                    viewModel.onEvent(PlayerEvent.DecreaseBrightness)
                                }
                            }
                            initialOffset = change.position.y
                        }
                    },
                    onDragEnd = {
                        viewModel.onUiEvent(PlayerUiEvent.ShowBrightnessBar(false))
                        viewModel.onUiEvent(PlayerUiEvent.ShowVolumeBar(false))
                    }
                )
            }
    ) {
        NextExoPlayer(
            onBackPressed = onBackPressed,
            changeOrientation = { requestedOrientation ->
                val activity = context.findActivity()
                activity?.requestedOrientation = requestedOrientation
                activity?.requestedOrientation?.let { viewModel.onEvent(PlayerEvent.ChangeOrientation(it)) }
            }
        )
        NextPlayerUI(
            onBackPressed = onBackPressed
        )
    }
}


