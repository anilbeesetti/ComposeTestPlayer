package com.arcticoss.feature.player.composables

import android.content.Context
import android.media.AudioManager
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.PlayerUiEvent
import com.arcticoss.feature.player.PlayerViewModel
import com.arcticoss.feature.player.utils.findActivity
import com.arcticoss.feature.player.utils.hideSystemBars
import com.arcticoss.feature.player.utils.showSystemBars
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay


private const val TAG = "NextPlayerUI"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextPlayerUI(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()
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
            viewModel.onUiEvent(PlayerUiEvent.ShowUi(false))
            context.findActivity()?.hideSystemBars()
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if(playerUiState.showUi) {
            PlayerUIHeader(
                title = "TODO",
                onBackPressed = onBackPressed,
                modifier = Modifier
                    .systemBarsPadding()
                    .align(Alignment.TopCenter)
            )
            PlayerUIMainControls(
                playPauseIcon = if (playerState.playWhenReady) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onPlayPauseClick = {
                    if (player.playWhenReady) {
                        player.pause()
                    } else {
                        player.play()
                    }
                },
                onSkipNextClick = { player.seekToNext() },
                onSkipPreviousClick = { player.seekToPrevious() },
                modifier = Modifier
                    .align(Alignment.Center)
            )
            PlayerUIFooter(
                duration = playerState.currentMediaItemDuration,
                currentPosition = playerState.currentPosition,
                onSeek = {
                    player.seekTo(it.toLong())
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
            )
        }
        if (playerUiState.showVolumeBar) {
            AudioAdjustmentBar(
                volumeLevel = playerState.currentVolume,
                maxVolumeLevel = playerState.maxLevel,
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterStart)
            )
        }
        if (playerUiState.showBrightnessBar) {
            BrightnessAdjustmentBar(
                brightness = playerState.currentBrightness,
                maxBrightness = playerState.maxLevel,
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


