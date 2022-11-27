package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.ResizeMode
import com.arcticoss.nextplayer.feature.player.Dialog
import com.arcticoss.nextplayer.feature.player.PersistableState
import com.arcticoss.nextplayer.feature.player.PlayerViewState
import com.arcticoss.nextplayer.feature.player.R
import com.arcticoss.nextplayer.feature.player.UIEvent
import com.arcticoss.nextplayer.feature.player.state.BrightnessState
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.arcticoss.nextplayer.feature.player.state.MediaState
import com.google.android.exoplayer2.video.VideoSize

@Composable
fun MediaPlayerScreenContent(
    currentMedia: Media,
    mediaState: MediaState,
    viewState: PlayerViewState,
    controller: ControllerState,
    preferences: PlayerPreferences,
    brightnessController: BrightnessState,
    onEvent: (UIEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        MediaPlayer(
            state = mediaState,
            modifier = Modifier
                .align(Alignment.Center),
            resizeMode = preferences.resizeMode
        )
        MediaGestures(
            mediaState = mediaState,
            controller = controller,
            brightnessState = brightnessController,
        )
        mediaState.playerState?.let {
            Subtitle(
                cueGroup = it.cueGroup,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        MediaControls(
            mediaState = mediaState,
            currentMedia = currentMedia,
            controller = controller,
            preferences = preferences,
            brightnessState = brightnessController,
            showDialog = { onEvent(UIEvent.ShowDialog(it)) },
            onSwitchAspectClick = { onEvent(UIEvent.SwitchResizeMode()) },
            onLockClick = mediaState::toggleControllerLock
        )

        // Show audio track selector dialog
        if (viewState.showDialog == Dialog.AudioTrack) {
            mediaState.playerState?.let { state ->
                TrackSelectorDialog(
                    title = { Text(text = stringResource(R.string.select_audio_track)) },
                    onDismiss = { onEvent(UIEvent.ShowDialog(Dialog.None)) },
                    tracks = state.audioTracks,
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.playerState?.let { playerState ->
                                val persistableState = PersistableState(
                                    index = playerState.mediaItemIndex,
                                    position = controller.positionMs,
                                    playWhenReady = playerState.playWhenReady,
                                    brightness = brightnessController.currentBrightness,
                                    audioTrackId = it.getTrackFormat(0).id
                                )
                                onEvent(UIEvent.SaveState(persistableState))
                            }
                        }
                    }
                )
            }
        }

        // Show subtitle track selector dialog
        if (viewState.showDialog == Dialog.SubtitleTrack) {
            mediaState.playerState?.let { state ->
                TrackSelectorDialog(
                    title = { Text(text = stringResource(R.string.select_subtitle_track)) },
                    onDismiss = { onEvent(UIEvent.ShowDialog(Dialog.None)) },
                    tracks = state.subtitleTracks,
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.playerState?.let { playerState ->
                                val persistableState = PersistableState(
                                    index = playerState.mediaItemIndex,
                                    position = controller.positionMs,
                                    playWhenReady = playerState.playWhenReady,
                                    brightness = brightnessController.currentBrightness,
                                    subtitleTrackId = it.getTrackFormat(0).id
                                )
                                onEvent(UIEvent.SaveState(persistableState))
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun MediaPlayer(
    state: MediaState,
    modifier: Modifier = Modifier,
    resizeMode: ResizeMode = ResizeMode.FitScreen
) {
    Box(
        modifier = modifier
            .run {
                val aspectRatio = state.playerState?.videoSize?.aspectRatio ?: 0F
                if (aspectRatio <= 0) fillMaxSize()
                else resize(aspectRatio, resizeMode)
            }
    ) {
        state.player?.let {
            ExoPlayerView(
                player = it,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


/**
 * Get aspect ratio from video size
 */
private val VideoSize.aspectRatio
    get() = if (height == 0) 0f else width * pixelWidthHeightRatio / height