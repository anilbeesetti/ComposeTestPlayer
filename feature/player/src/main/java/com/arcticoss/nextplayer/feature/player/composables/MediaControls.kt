package com.arcticoss.nextplayer.feature.player.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.FitScreen
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material.icons.rounded.ZoomOutMap
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.ResizeMode
import com.arcticoss.nextplayer.feature.player.Dialog
import com.arcticoss.nextplayer.feature.player.state.BrightnessState
import com.arcticoss.nextplayer.feature.player.state.ControllerBar
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.arcticoss.nextplayer.feature.player.state.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.state.MediaState
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
    preferences: PlayerPreferences,
    brightnessState: BrightnessState,
    showDialog: (Dialog) -> Unit,
    onLockClick: () -> Unit,
    onRotationClick: () -> Unit,
    onSwitchAspectClick: () -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()
    var scrubbing by remember { mutableStateOf(false) }
    var interactingWithControllerTrigger by remember { mutableStateOf(0) }

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    LaunchedEffect(mediaState.controllerVisibility, mediaState.isControllerLocked) {
        if (mediaState.isControllerLocked) {
            activity?.hideSystemBars()
        } else {
            when (mediaState.controllerVisibility) {
                ControllerVisibility.Visible -> activity?.showSystemBars()
                ControllerVisibility.Invisible,
                ControllerVisibility.PartiallyVisible -> activity?.hideSystemBars()
            }
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

            LaunchedEffect(hideWhenTimeout, interactingWithControllerTrigger) {
                if (hideWhenTimeout) {
                    // hide after 3s
                    delay(3000)
                    mediaState.isControllerShowing = false
                }
            }

            if (mediaState.isControllerLocked) {
                IconButton(
                    onClick = {
                        interactingWithControllerTrigger++
                        onLockClick()
                    },
                    modifier = Modifier
                        .statusBarsPadding()
                        .align(Alignment.TopStart)
                ) {
                    Icon(imageVector = Icons.Rounded.Lock, contentDescription = "")
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
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
        }
        if (mediaState.controllerVisibility.isShowing && !mediaState.isControllerLocked) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter),
            ) {
                val duration =
                    if (controller.durationMs == C.TIME_UNSET) currentMedia.duration else controller.durationMs
                TimeAndSeekbar(
                    positionMs = controller.positionMs,
                    durationMs = duration,
                    showOverlay = mediaState.controllerVisibility == ControllerVisibility.PartiallyVisible,
                    onScrubStart = { scrubbing = true },
                    onScrubMove = {
                        if (mediaState.playerState?.playbackState == Player.STATE_READY) {
                            controller.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                            controller.seekTo(it)
                        }
                    },
                    onScrubStop = {
                        controller.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                        controller.seekTo(it)
                        scrubbing = false
                    }
                )
                if (mediaState.controllerVisibility == ControllerVisibility.Visible) {
                    Controls(
                        resizeMode = preferences.resizeMode,
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
                        onAspectRatioClick = {
                            interactingWithControllerTrigger++
                            onSwitchAspectClick()
                        },
                        onLockClick = {
                            interactingWithControllerTrigger++
                            onLockClick()
                        },
                        onRotationClick = {
                            interactingWithControllerTrigger++
                            onRotationClick()
                        }
                    )
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
fun Controls(
    resizeMode: ResizeMode,
    modifier: Modifier = Modifier,
    onAspectRatioClick: () -> Unit,
    onLockClick: () -> Unit,
    onRotationClick: () -> Unit
) {
    val resizeModeIcon = when (resizeMode) {
        ResizeMode.FitScreen -> Icons.Rounded.FitScreen
        ResizeMode.FixedWidth -> Icons.Rounded.Crop
        ResizeMode.FixedHeight -> Icons.Rounded.Crop
        ResizeMode.Fill -> Icons.Rounded.AspectRatio
        ResizeMode.Zoom -> Icons.Rounded.ZoomOutMap
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            ControlButton(
                onClick = onLockClick,
                icon = Icons.Rounded.LockOpen
            )
            ControlButton(
                onClick = onRotationClick,
                icon = Icons.Rounded.ScreenRotation
            )
        }
        Row {
            ControlButton(
                onClick = onAspectRatioClick,
                icon = resizeModeIcon
            )
        }
    }
}

@Composable
fun ControlButton(
    onClick: () -> Unit,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(15.dp)
    ) {
        Icon(imageVector = icon, contentDescription = icon.name)
    }
}


/**
 * Get max music stream volume
 */
val AudioManager.maxStreamMusicVolume: Int
    get() = getStreamMaxVolume(AudioManager.STREAM_MUSIC)