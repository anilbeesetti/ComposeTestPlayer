package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.player.ui.playerscreen.NextPlayerViewModel
import com.arcticoss.nextplayer.player.ui.playerscreen.PlayerUiEvent
import com.arcticoss.nextplayer.player.utils.BrightnessController
import com.arcticoss.nextplayer.player.utils.findActivity
import com.arcticoss.nextplayer.player.utils.hideSystemBars
import com.arcticoss.nextplayer.player.utils.showSystemBars
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay
import java.io.File


private const val TAG = "NextPlayerUI"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextPlayerUI(
    path: String,
    viewModel: NextPlayerViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()
    val enterTransition = fadeIn(animationSpec = tween(100))
    val exitTransition = fadeOut(animationSpec = tween(100))
    val context = LocalContext.current
    val player = viewModel.player as ExoPlayer
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    LaunchedEffect(key1 = playerUiState.showUi, key2 = playerState.isPlaying) {
        if (playerUiState.showUi) {
            context.findActivity()?.showSystemBars()
        } else {
            context.findActivity()?.hideSystemBars()
        }
        if (playerState.isPlaying and playerUiState.showUi) {
            delay(3000)
            viewModel.onUiEvent(PlayerUiEvent.showUi(false))
            context.findActivity()?.hideSystemBars()
        }
    }
    val file = File(path)
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = playerUiState.showUi,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PlayerUIHeader(title = file.name, onBackPressed = onBackPressed)
                PlayerUIFooter(
                    duration = playerState.currentMediaItemDuration,
                    currentPosition = playerState.currentPosition,
                    onSeek = {
                        player.seekTo(it.toLong())
                    }
                )
            }
        }
        VerticalSwipeMediaControls(
            showVolumeBar = playerUiState.showVolumeBar,
            showBrightnessBar = playerUiState.showBrightnessBar,
            volumeLevel = playerState.currentVolumeLevel,
            brightness = playerState.currentBrightness,
            maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            maxBrightness = BrightnessController.MAX_BRIGHTNESS,
            modifier = Modifier
                .heightIn(max = 500.dp)
                .align(Alignment.Center),
            dismissBrightnessBar = { viewModel.onUiEvent(PlayerUiEvent.showBrightnessBar(false)) },
            dismissVolumeBar = { viewModel.onUiEvent(PlayerUiEvent.showVolumeBar(false)) }
        )
        PlayerUIMainControls(
            show = playerUiState.showUi,
            playPauseIcon = if (playerState.playWhenReady) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            onPlayPauseClick = {
                if (player.playWhenReady) {
                    player.pause()
                } else {
                    player.play()
                }
            },
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            onSkipNextClick = { player.seekToNext() },
            onSkipPreviousClick = { player.seekToPrevious() },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


