package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.feature.player.Dialog
import com.arcticoss.nextplayer.feature.player.presentation.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.presentation.MediaState
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.arcticoss.nextplayer.feature.player.utils.TimeUtils
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.hideSystemBars
import com.arcticoss.nextplayer.feature.player.utils.showSystemBars
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters

@Composable
fun MediaControls(
    currentMedia: Media,
    mediaState: MediaState,
    controller: ControllerState,
    showDialog: (Dialog) -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(key1 = mediaState.isControllerShowing) {
        when (mediaState.isControllerShowing) {
            true -> activity?.showSystemBars()
            false -> activity?.hideSystemBars()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val isBufferingShowing by remember {
            derivedStateOf {
                mediaState.playerState?.run {
                    playbackState == Player.STATE_BUFFERING && videoFormat == null
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
                title = currentMedia.title,
                onBackClick = { activity?.finish() },
                onAudioIconClick = { showDialog(Dialog.AudioTrack) },
                onSubtitleIconClick = { showDialog(Dialog.SubtitleTrack) },
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(top = 5.dp)
                    .align(Alignment.TopCenter)
            )
            PlayerUIMainControls(
                playPauseIcon = if (controller.showPause) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                onPlayPauseClick = {
                    controller.playOrPause()
                },
                onSkipNextClick = { mediaState.player?.seekToNext() },
                onSkipPreviousClick = { mediaState.player?.seekToPrevious() },
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
        if (mediaState.isControllerShowing || mediaState.controllerVisibility == ControllerVisibility.PartiallyVisible) {
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

                val duration = if (controller.durationMs == C.TIME_UNSET) currentMedia.duration else controller.durationMs

                Slider(
                    value = controller.positionMs.toFloat(),
                    valueRange = 0F..duration.toFloat(),
                    onValueChange = {
                        if (mediaState.playerState?.playbackState == Player.STATE_READY) {
                            (mediaState.player as? ExoPlayer)?.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                            mediaState.player?.seekTo(it.toLong())
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = TimeUtils.formatTime(context, controller.durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }
    }
}