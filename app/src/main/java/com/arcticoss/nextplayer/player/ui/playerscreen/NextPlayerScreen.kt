package com.arcticoss.nextplayer.player.ui.playerscreen

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.NextExoPlayer
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.NextPlayerUI
import com.arcticoss.nextplayer.player.utils.BrightnessController
import com.arcticoss.nextplayer.player.utils.findActivity
import com.google.android.exoplayer2.ExoPlayer
import kotlin.math.abs


private const val TAG = "NextPlayerScreen"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextPlayerScreen(
    viewModel: NextPlayerViewModel = hiltViewModel(),
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
                        if (isOffsetEnough or isDragEnough) {
                            if (playerUiState.showVolumeBar) {
                                if (change.position.y - initialOffset < 0) {
                                    audioManager.adjustStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FLAG_PLAY_SOUND
                                    )
                                } else {
                                    audioManager.adjustStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_LOWER,
                                        AudioManager.FLAG_PLAY_SOUND
                                    )
                                }
                                viewModel.updateVolumeLevel(
                                    audioManager.getStreamVolume(
                                        AudioManager.STREAM_MUSIC
                                    )
                                )
                            }
                            if (playerUiState.showBrightnessBar) {
                                activity?.let {
                                    if (change.position.y - initialOffset < 0) {
                                        BrightnessController.increaseBrightness(
                                            it,
                                            playerState.currentBrightness,
                                            onBrightnessChanged = { newBrightness ->
                                                viewModel.updateBrightness(newBrightness)
                                            }
                                        )
                                    } else {
                                        BrightnessController.decreaseBrightness(
                                            it,
                                            playerState.currentBrightness,
                                            onBrightnessChanged = { newBrightness ->
                                                viewModel.updateBrightness(newBrightness)
                                            }
                                        )
                                    }
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
                activity?.requestedOrientation?.let { viewModel.updateScreenOrientation(it) }
            }
        )
        NextPlayerUI(
            onBackPressed = onBackPressed
        )
    }
}


