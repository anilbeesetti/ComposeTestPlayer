package com.arcticoss.nextplayer.feature.player.composables

import Subtitle
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.ResizeMode
import com.arcticoss.nextplayer.core.ui.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.player.Dialog
import com.arcticoss.nextplayer.feature.player.PersistableState
import com.arcticoss.nextplayer.feature.player.PlayerViewModel
import com.arcticoss.nextplayer.feature.player.PlayerViewState
import com.arcticoss.nextplayer.feature.player.R
import com.arcticoss.nextplayer.feature.player.UIEvent
import com.arcticoss.nextplayer.feature.player.state.BrightnessState
import com.arcticoss.nextplayer.feature.player.state.ControllerState
import com.arcticoss.nextplayer.feature.player.state.MediaState
import com.arcticoss.nextplayer.feature.player.state.rememberBrightnessState
import com.arcticoss.nextplayer.feature.player.state.rememberControllerState
import com.arcticoss.nextplayer.feature.player.state.rememberManagedExoPlayer
import com.arcticoss.nextplayer.feature.player.state.rememberMediaState
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.keepScreenOn
import com.google.android.exoplayer2.C.TRACK_TYPE_AUDIO
import com.google.android.exoplayer2.C.TRACK_TYPE_TEXT
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import com.google.android.exoplayer2.video.VideoSize
import java.io.File
import java.util.*


private const val TAG = "VideoScreen"

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    val player by rememberManagedExoPlayer()
    val mediaState = rememberMediaState(player = player)
    val controller = rememberControllerState(mediaState = mediaState)
    val brightnessController = rememberBrightnessState(activity = activity)

    val playerViewState by viewModel.playerViewState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()

    MediaPlayerScreen(
        mediaState = mediaState,
        controller = controller,
        brightnessController = brightnessController,
        viewState = playerViewState,
        preferences = preferences,
        player = player,
        onEvent = viewModel::onEvent
    )

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


private fun Player.switchTrack(trackGroup: Tracks.Group) {
    if (!trackGroup.isSelected && trackGroup.isSupported) {
        this.trackSelectionParameters = this
            .trackSelectionParameters
            .buildUpon()
            .setOverrideForType(
                TrackSelectionOverride(trackGroup.mediaTrackGroup, 0)
            ).build()
    }
}


private fun Player.getTrackGroupFromFormatId(trackType: Int, id: String): Tracks.Group? {
    for (group in this.currentTracks.groups) {
        if (group.type == trackType) {
            val trackGroup = group.mediaTrackGroup
            val format: Format = trackGroup.getFormat(0)
            if (Objects.equals(id, format.id)) {
                return group
            }
        }
    }
    return null
}

val Format.isPortrait: Boolean
    get() {
        val isRotated = this.rotationDegrees == 90 || this.rotationDegrees == 270
        return if (isRotated) {
            this.width > this.height
        } else {
            this.height > this.width
        }
    }

val VideoSize.aspectRatio
    get() = if (height == 0) 0f else width * pixelWidthHeightRatio / height


@SuppressLint("SourceLockedOrientationActivity")
@Composable
internal fun MediaPlayerScreen(
    mediaState: MediaState,
    controller: ControllerState,
    brightnessController: BrightnessState,
    viewState: PlayerViewState,
    preferences: PlayerPreferences,
    player: Player?,
    onEvent: (UIEvent) -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current


    val currentMedia = remember(viewState.mediaList, mediaState.playerState?.mediaItemIndex) {
        if (viewState.mediaList.isNotEmpty()) {
            mediaState.playerState?.let { viewState.mediaList[it.mediaItemIndex] } ?: Media()
        } else Media()
    }

    /**
     * Changing tracks to previous remembered tracks on tracks changes
     */
    LaunchedEffect(currentMedia, mediaState.playerState?.audioTracks) {
        currentMedia.audioTrackId?.let { id ->
            val trackGroup = mediaState.player?.getTrackGroupFromFormatId(TRACK_TYPE_AUDIO, id)
            trackGroup?.let {
                mediaState.player?.switchTrack(it)
            }
        }
        currentMedia.subtitleTrackId?.let { id ->
            val trackGroup = mediaState.player?.getTrackGroupFromFormatId(TRACK_TYPE_TEXT, id)
            trackGroup?.let {
                mediaState.player?.switchTrack(it)
            }
        }
    }

    /**
     * Handling rotation on video format change
     */
    LaunchedEffect(mediaState.playerState?.videoFormat) {
        mediaState.playerState?.videoFormat?.let {
            if (it.isPortrait) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
    }

    /**
     * Handling screen onState while media is playing
     */
    LaunchedEffect(mediaState.playerState?.isPlaying) {
        activity?.keepScreenOn(mediaState.playerState?.isPlaying == true)
    }

    /**
     * Restoring media state on mediaItemIndexChange
     */
    LaunchedEffect(mediaState.playerState?.mediaItemIndex) {
        mediaState.playerState?.let {
            if (viewState.mediaList.isNotEmpty()) {
                val position = viewState.mediaList[it.mediaItemIndex].lastPlayedPosition
                player?.seekTo(position)
            }
        }
    }

    /**
     * Restoring brightness level
     */
    LaunchedEffect(preferences.saveBrightnessLevel, preferences.brightnessLevel) {
        Log.d(TAG, "VideoScreen: save")
        if (preferences.saveBrightnessLevel) {
            brightnessController.setBrightness(preferences.brightnessLevel)
        }
    }

    /**
     * Sets [MediaItem] list to player
     */
    LaunchedEffect(player, viewState.mediaList.size) {
        player?.run {
            val mediaItems = viewState.mediaList.map {
                MediaItem.Builder()
                    .setUri(File(it.path).toUri())
                    .setMediaId(it.id.toString())
                    .build()
            }
            if (mediaItems.isNotEmpty()) {
                this.setMediaItems(mediaItems)
                viewState.currentMediaItemId?.let { id ->
                    val index = viewState.mediaList.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        val media = viewState.mediaList[index]
                        this.seekTo(index, media.lastPlayedPosition)
                    }
                }
                playWhenReady = viewState.playWhenReady
                this.prepare()
            }
        }
    }


    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->

        /**
         * Saving media state on pause
         */
        if (event == Lifecycle.Event.ON_PAUSE) {
            mediaState.playerState?.let { playerState ->
                val state = PersistableState(
                    index = playerState.mediaItemIndex,
                    position = controller.positionMs,
                    playWhenReady = playerState.playWhenReady,
                    brightness = brightnessController.currentBrightness,
                )
                onEvent(UIEvent.SaveState(state))
            }
        }

        /**
         * Removing controller lock on resume
         */
        if (event == Lifecycle.Event.ON_RESUME) {
            mediaState.isControllerLocked = false
        }
    }


    DisposableEffect(player) {
        val listener = object : Player.Listener {

            /**
             * Saving media state on mediaItemTransition
             */
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaState.playerState?.oldPosition?.let {
                    if (mediaState.playerState?.firstFrameRendered == true) {
                        val position =
                            if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO) 0 else it.positionMs
                        val state = PersistableState(
                            index = it.mediaItemIndex,
                            position = position,
                            playWhenReady = player?.playWhenReady == true
                        )
                        onEvent(UIEvent.SaveState(state))
                    }
                }
            }
        }

        player?.addListener(listener)

        // Dispose
        onDispose { player?.removeListener(listener) }
    }


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
