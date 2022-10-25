package com.arcticoss.feature.player.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.PlayerViewModel


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
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val player = viewModel.player

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (playerUiState.isControllerVisible) {
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
        }
        PlayerUIFooter(
            playerUiState = playerUiState,
            preferences = preferences,
            duration = playerState.currentMediaItemDuration,
            currentPosition = playerState.currentPosition,
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
            onSeek = { viewModel.seekTo(it.toLong()) },
            onAspectRatioClick = { viewModel.switchAspectRatio() },
            onLockClick = {  }
        )
        if (playerUiState.isVolumeBarVisible) {
            AudioAdjustmentBar(
                volumeLevel = playerState.volumeLevel,
                maxVolumeLevel = playerState.maxLevel,
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterStart)
            )
        }
        if (playerUiState.isBrightnessBarVisible) {
            BrightnessAdjustmentBar(
                brightness = playerState.brightnessLevel,
                maxBrightness = playerState.maxLevel,
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


