package com.arcticoss.nextplayer.feature.player.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.feature.player.presentation.MediaState
import com.arcticoss.nextplayer.feature.player.presentation.composables.PlayerUIHeader
import com.arcticoss.nextplayer.feature.player.presentation.composables.PlayerUIMainControls
import com.arcticoss.nextplayer.feature.player.presentation.rememberControllerState
import com.arcticoss.nextplayer.feature.player.utils.TimeUtils
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters

@Composable
fun MediaControls(
    currentMedia: Media,
    mediaState: MediaState
) {

    val context = LocalContext.current
    val controller = rememberControllerState(mediaState = mediaState)
    val activity = context.findActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val isBufferingShowing by remember {
            derivedStateOf {
                mediaState.playerState?.run {
                    playbackState == Player.STATE_BUFFERING
                } ?: false
            }
        }
        if (isBufferingShowing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (mediaState.isControllerShowing) {
            PlayerUIHeader(
                title = currentMedia.title ?: "",
                onBackClick = { activity?.finish() },
                onAudioTrackButtonClick = {},
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(top = 5.dp)
                    .align(Alignment.TopCenter)
            )
            PlayerUIMainControls(
                playPauseIcon = if (mediaState.playerState?.isPlaying == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onPlayPauseClick = {
                    controller.playOrPause()
                },
                onSkipNextClick = { mediaState.player?.seekToNext() },
                onSkipPreviousClick = { mediaState.player?.seekToPrevious() },
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 30.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = TimeUtils.formatTime(context, controller.positionMs),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            TimeBar(
                durationMs = controller.durationMs,
                positionMs = controller.positionMs,
                bufferedPositionMs = controller.bufferedPositionMs,
                onScrubMove = {
                    (mediaState.player as? ExoPlayer)?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                    mediaState.player?.seekTo(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                contentPadding = PaddingValues(24.dp),
            )

            Text(
                text = TimeUtils.formatTime(context, controller.durationMs),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
        }
    }
}