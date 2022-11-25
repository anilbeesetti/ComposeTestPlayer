package com.arcticoss.nextplayer.feature.player.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitScreen
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.feature.player.Dialog
import com.arcticoss.nextplayer.feature.player.state.BrightnessState
import com.arcticoss.nextplayer.feature.player.state.ControllerBar
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.arcticoss.nextplayer.feature.player.state.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.state.MediaState
import com.arcticoss.nextplayer.feature.player.utils.TimeUtils
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.hideSystemBars
import com.arcticoss.nextplayer.feature.player.utils.showSystemBars
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import kotlinx.coroutines.delay

@Composable
fun MediaControls(
    currentMedia: Media,
    mediaState: MediaState,
    controller: ControllerState,
    brightnessState: BrightnessState,
    showDialog: (Dialog) -> Unit,
    switchAspectRatio: () -> Unit,
) {

    val context = LocalContext.current
    val activity = context.findActivity()
    var scrubbing by remember { mutableStateOf(false) }

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    LaunchedEffect(key1 = mediaState.controllerVisibility) {
        when (mediaState.controllerVisibility) {
            ControllerVisibility.Visible -> activity?.showSystemBars()
            ControllerVisibility.Invisible,
            ControllerVisibility.PartiallyVisible -> activity?.hideSystemBars()
        }
    }

    val hideWhenTimeout = !mediaState.shouldShowControllerIndefinitely && !scrubbing

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
        if (mediaState.controllerVisibility == ControllerVisibility.Visible) {

            LaunchedEffect(key1 = hideWhenTimeout) {
                if (hideWhenTimeout) {
                    // hide after 3s
                    delay(3000)
                    mediaState.isControllerShowing = false
                }
            }

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
        if (mediaState.controllerVisibility.isShowing) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 10.dp)
                    .align(Alignment.BottomCenter),
            ) {
                val duration =
                    if (controller.durationMs == C.TIME_UNSET) currentMedia.duration / 1000 else controller.durationMs
                TimeAndSeekbar(
                    duration = duration,
                    mediaState = mediaState,
                    controller = controller,
                    onScrubbingStarted = { scrubbing = true },
                    onScrubbingStopped = { scrubbing = false }
                )
                if (mediaState.controllerVisibility == ControllerVisibility.Visible) {
                    Row {
                        IconButton(onClick = switchAspectRatio) {
                            Icon(
                                imageVector = Icons.Rounded.FitScreen,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
        if (mediaState.controllerBar == ControllerBar.Volume) {
            AudioAdjustmentBar(
                volumeLevel = mediaState.playerState?.deviceVolume ?: 0,
                maxVolumeLevel = audioManager.maxStreamMusicVolume,
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterStart)
            )
        }
        if (mediaState.controllerBar == ControllerBar.Brightness) {
            BrightnessAdjustmentBar(
                brightness = brightnessState.currentBrightness,
                maxBrightness = brightnessState.maxBrightness,
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .fillMaxHeight(0.6f)
                    .padding(20.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
private fun TimeAndSeekbar(
    duration: Long,
    mediaState: MediaState,
    controller: ControllerState,
    modifier: Modifier = Modifier,
    onScrubbingStarted: (() -> Unit)? = null,
    onScrubbingStopped: (() -> Unit)? = null
) {

    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = TimeUtils.formatTime(context, controller.positionMs),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
        SeekBar(
            durationMs = duration,
            positionMs = controller.positionMs,
            onScrubStart = {
                onScrubbingStarted?.invoke()
            },
            onScrubMove = {
                if (mediaState.playerState?.playbackState == Player.STATE_READY) {
                    controller.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                    controller.seekTo(it)
                }
            },
            onScrubStop = {
                controller.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                controller.seekTo(it)
                onScrubbingStopped?.invoke()
            },
            modifier = Modifier
                .weight(1f),
        )

        Text(
            text = TimeUtils.formatTime(context, duration),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 5.dp)
        )
    }
}


/**
 * Get max music stream volume
 */
val AudioManager.maxStreamMusicVolume: Int
    get() = getStreamMaxVolume(AudioManager.STREAM_MUSIC)