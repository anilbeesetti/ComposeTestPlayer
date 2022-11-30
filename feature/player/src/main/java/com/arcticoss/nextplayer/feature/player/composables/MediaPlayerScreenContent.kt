package com.arcticoss.nextplayer.feature.player.composables

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.datetime.Clock
import java.util.*

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

    val context = LocalContext.current
    val activity = context.findActivity()

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
        Subtitle(
            mediaState = mediaState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        MediaControls(
            mediaState = mediaState,
            currentMedia = currentMedia,
            controller = controller,
            preferences = preferences,
            brightnessState = brightnessController,
            showDialog = { onEvent(UIEvent.ShowDialog(it)) },
            onSwitchAspectClick = { onEvent(UIEvent.SwitchResizeMode()) },
            onLockClick = mediaState::toggleControllerLock,
            onRotationClick = { activity?.setNextOrientation() }
        )

        // Show audio track selector dialog
        if (viewState.showDialog == Dialog.AudioTrack) {
            mediaState.playerState?.let { state ->
                TrackSelectorDialog(
                    title = { Text(text = stringResource(R.string.select_audio_track)) },
                    onDismiss = { onEvent(UIEvent.ShowDialog(Dialog.None)) },
                    tracks = state.audioTracks.map(Tracks.Group::toTrack),
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.playerState?.let { playerState ->
                                val persistableState = PersistableState(
                                    index = playerState.mediaItemIndex,
                                    position = controller.positionMs,
                                    playWhenReady = playerState.playWhenReady,
                                    brightness = brightnessController.currentBrightness,
                                    playedOn = Clock.System.now().toEpochMilliseconds(),
                                    audioTrackId = it.id
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
                    tracks = state.subtitleTracks.map(Tracks.Group::toTrack),
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.playerState?.let { playerState ->
                                val persistableState = PersistableState(
                                    index = playerState.mediaItemIndex,
                                    position = controller.positionMs,
                                    playWhenReady = playerState.playWhenReady,
                                    brightness = brightnessController.currentBrightness,
                                    playedOn = Clock.System.now().toEpochMilliseconds(),
                                    subtitleTrackId = it.id
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


@SuppressLint("SourceLockedOrientationActivity")
private fun Activity.setNextOrientation() {
    val isLandscape = requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            || requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    requestedOrientation = if (isLandscape) {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    } else {
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    Log.d("TAG", "setNextOrientation: $requestedOrientation")
}

fun Tracks.Group.toTrack() = Track(
    id = getTrackFormat(0).id,
    name = getTrackFormat(0).displayName(),
    isSelected = isSelected,
    isSupported = isSupported
)


private fun Format.displayName(): String {
    var displayName = ""
    this.language?.let {
        displayName += if (this.language != "und") {
            Locale(this.language.toString()).displayLanguage
        } else {
            this.sampleMimeType
        }
    }
    this.label?.let {
        displayName += "," + this.label
    }
    return displayName
}