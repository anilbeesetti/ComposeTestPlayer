package com.arcticoss.nextplayer.feature.player.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.PlayerState
import com.arcticoss.nextplayer.feature.player.PlayerUiState
import com.arcticoss.nextplayer.feature.player.UiEvent
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import kotlin.math.abs


@Composable
fun NextPlayerUI(
    player: ExoPlayer,
    playerState: PlayerState,
    playerUiState: PlayerUiState,
    preferences: PlayerPreferences,
    currentPosition: Long,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onUiEvent: (UiEvent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (playerState.playbackState == Player.STATE_BUFFERING && !playerState.playbackStarted) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (playerUiState.isControllerVisible) {
            PlayerUIHeader(
                title = playerState.currentPlayingMedia.title,
                onBackClick = onBackPressed,
                onAudioTrackButtonClick = {onUiEvent(UiEvent.ShowAudioTrackDialog(true))},
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(top = 5.dp)
                    .align(Alignment.TopCenter)
            )
            PlayerUIMainControls(
                playPauseIcon = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onPlayPauseClick = {
                    if (player.playWhenReady) {
                        player.pause()
                    } else {
                        player.play()
                    }
                },
                onSkipNextClick = player::seekToNext,
                onSkipPreviousClick = player::seekToPrevious,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        PlayerUIFooter(
            playerUiState = playerUiState,
            preferences = preferences,
            duration = playerState.currentPlayingMedia.duration / 1000,
            currentPosition = currentPosition,
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
            onSeek = {
                val offset = it - player.currentPosition
                if (abs(offset) > 1000) {
                    player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                    player.seekTo(it.toLong())
                }
            },
            onAspectRatioClick = { onUiEvent(UiEvent.ToggleAspectRatio) },
            onLockClick = { }
        )
        if (playerUiState.isVolumeBarVisible) {
            AudioAdjustmentBar(
                volumeLevel = playerState.volume,
                maxVolumeLevel = playerState.maxLevel,
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterStart)
            )
        }
        if (playerUiState.isBrightnessBarVisible) {
            BrightnessAdjustmentBar(
                brightness = playerState.brightness,
                maxBrightness = playerState.maxLevel,
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


