package com.arcticoss.nextplayer.feature.player.composables

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.ui.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.player.PersistableState
import com.arcticoss.nextplayer.feature.player.PlayerViewModel
import com.arcticoss.nextplayer.feature.player.PlayerViewState
import com.arcticoss.nextplayer.feature.player.UIEvent
import com.arcticoss.nextplayer.feature.player.state.rememberBrightnessState
import com.arcticoss.nextplayer.feature.player.state.rememberControllerState
import com.arcticoss.nextplayer.feature.player.state.rememberManagedExoPlayer
import com.arcticoss.nextplayer.feature.player.state.rememberMediaState
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.keepScreenOn
import com.google.android.exoplayer2.C.TRACK_TYPE_AUDIO
import com.google.android.exoplayer2.C.TRACK_TYPE_TEXT
import com.google.android.exoplayer2.C.TrackType
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import kotlinx.datetime.Clock
import java.io.File
import java.util.*


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val playerViewState by viewModel.playerViewState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()

    MediaPlayerScreen(
        viewState = playerViewState,
        preferences = preferences,
        onEvent = viewModel::onEvent
    )

}


@SuppressLint("SourceLockedOrientationActivity")
@Composable
internal fun MediaPlayerScreen(
    viewState: PlayerViewState,
    preferences: PlayerPreferences,
    onEvent: (UIEvent) -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    val player by rememberManagedExoPlayer()
    val mediaState = rememberMediaState(player = player)
    val controller = rememberControllerState(mediaState = mediaState)
    val brightnessController = rememberBrightnessState(activity = activity)

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
     * Handling screen timeout while media is playing
     */
    LaunchedEffect(mediaState.playerState?.isPlaying) {
        activity?.keepScreenOn(mediaState.playerState?.isPlaying == true)
    }

    /**
     * Restoring media state on mediaItemIndexChange
     */
    LaunchedEffect(mediaState.playerState?.mediaItemIndex) {
        mediaState.playerState?.let { playerState ->
            if (viewState.mediaList.isNotEmpty()) {
                val media = viewState.mediaList[playerState.mediaItemIndex]
                val position = media.lastPlayedPosition.takeIf { it != media.duration } ?: 0
                controller.seekTo(position)
            }
        }
    }

    /**
     * Restoring brightness level
     */
    LaunchedEffect(preferences.saveBrightnessLevel, preferences.brightnessLevel) {
        if (preferences.saveBrightnessLevel) {
            brightnessController.setBrightness(preferences.brightnessLevel)
        }
    }

    /**
     * Sets [MediaItem] list to player
     */
    LaunchedEffect(mediaState.player, viewState.mediaList.size) {
        mediaState.player?.run {
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
                        val position = media.lastPlayedPosition.takeIf { it != media.duration } ?: 0
                        this.seekTo(index, position)
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
                    playedOn = Clock.System.now().toEpochMilliseconds(),
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


    DisposableEffect(mediaState.player) {
        val listener = object : Player.Listener {

            /**
             * Saving media state on mediaItemTransition
             */
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaState.playerState?.oldPosition?.let {
                    if (mediaState.playerState?.firstFrameRendered == true) {
                        val state = PersistableState(
                            index = it.mediaItemIndex,
                            position = it.positionMs,
                            playedOn = Clock.System.now().toEpochMilliseconds(),
                            playWhenReady = mediaState.player?.playWhenReady == true
                        )
                        onEvent(UIEvent.SaveState(state))
                    }
                }
            }

            /**
             * Close activity on [Player.STATE_ENDED]
             */
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == STATE_ENDED) {
                    activity?.finish()
                }
            }
        }

        // Add listener
        mediaState.player?.addListener(listener)

        // Remove listener on dispose
        onDispose { mediaState.player?.removeListener(listener) }
    }


    MediaPlayerScreenContent(
        currentMedia = currentMedia,
        mediaState = mediaState,
        viewState = viewState,
        controller = controller,
        preferences = preferences,
        brightnessController = brightnessController,
        onEvent = onEvent
    )
}

/**
 * switch track to the specified track
 */
private fun Player.switchTrack(trackGroup: Tracks.Group) {
    if (!trackGroup.isSelected && trackGroup.isSupported) {
        this.trackSelectionParameters = this
            .trackSelectionParameters
            .buildUpon()
            .setOverrideForType(TrackSelectionOverride(trackGroup.mediaTrackGroup, 0))
            .build()
    }
}

/**
 * Get [Tracks.Group] from format id
 * @param trackType what track type it is
 * @param id format id
 */
private fun Player.getTrackGroupFromFormatId(trackType: @TrackType Int, id: String): Tracks.Group? {
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

/**
 * Whether the format is portrait or not
 */
private val Format.isPortrait: Boolean
    get() {
        val isRotated = this.rotationDegrees == 90 || this.rotationDegrees == 270
        return if (isRotated) this.width > this.height else this.height > this.width
    }
