package com.arcticoss.nextplayer.feature.player.composables

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.core.model.AspectRatio
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.ui.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.player.*
import com.arcticoss.nextplayer.feature.player.state.MediaState
import com.arcticoss.nextplayer.feature.player.state.aspectRatio
import com.arcticoss.nextplayer.feature.player.state.isPortrait
import com.arcticoss.nextplayer.feature.player.state.rememberBrightnessState
import com.arcticoss.nextplayer.feature.player.state.rememberControllerState
import com.arcticoss.nextplayer.feature.player.state.rememberMediaState
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.keepScreenOn
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.text.CueGroup
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import java.io.File
import java.util.*


private const val TAG = "VideoScreen"

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun VideoScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val player by rememberManagedExoPlayer()
    val mediaState = rememberMediaState(player = player)
    val controller = rememberControllerState(mediaState = mediaState)
    val playerViewState by viewModel.playerViewState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context.findActivity()
    val brightnessController = rememberBrightnessState(activity = activity)

    /**
     * Handling rotation on video format change
     */
    LaunchedEffect(key1 = mediaState.playerState?.videoFormat) {
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
    LaunchedEffect(key1 = mediaState.playerState?.isPlaying) {
        activity?.keepScreenOn(mediaState.playerState?.isPlaying == true)
    }

    /**
     * Restoring media state on mediaItemIndexChange
     */
    LaunchedEffect(mediaState.playerState?.mediaItemIndex) {
        mediaState.playerState?.let {
            if (playerViewState.mediaList.isNotEmpty()) {
                val position = playerViewState.mediaList[it.mediaItemIndex].lastPlayedPosition
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
    LaunchedEffect(player, playerViewState.mediaList.size) {
        player?.run {
            val mediaItems = playerViewState.mediaList.map {
                MediaItem.Builder()
                    .setUri(File(it.path).toUri())
                    .setMediaId(it.id.toString())
                    .build()
            }
            if (mediaItems.isNotEmpty()) {
                this.setMediaItems(mediaItems)
                playerViewState.currentMediaItemId?.let { id ->
                    val index = playerViewState.mediaList.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        val media = playerViewState.mediaList[index]
                        seekTo(index, media.lastPlayedPosition)
                    }
                }
                playWhenReady = playerViewState.playWhenReady
                this.prepare()
            }
        }
    }

    /**
     * Saving media state on pause
     */
    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) {
        if (it == Lifecycle.Event.ON_PAUSE) {
            mediaState.playerState?.let { playerState ->
                viewModel.saveState(
                    index = playerState.mediaItemIndex,
                    position = controller.positionMs,
                    playWhenReady = playerState.playWhenReady,
                    brightness = brightnessController.currentBrightness
                )
            }
        }
    }

    /**
     * Saving media state on mediaItemTransition
     */
    DisposableEffect(player) {

        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaState.playerState?.oldPosition?.let {
                    if (mediaState.playerState?.firstFrameRendered == true) {
                        if (reason == MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            viewModel.saveState(
                                index = it.mediaItemIndex,
                                position = 0,
                                playWhenReady = player?.playWhenReady == true
                            )
                        } else {
                            viewModel.saveState(
                                index = it.mediaItemIndex,
                                position = it.positionMs,
                                playWhenReady = player?.playWhenReady == true
                            )
                        }
                    }
                }
            }
        }

        player?.addListener(listener)
        onDispose { player?.removeListener(listener) }
    }

    val currentMedia by remember {
        derivedStateOf {
            if (playerViewState.mediaList.isNotEmpty()) {
                mediaState.playerState?.let {
                    playerViewState.mediaList[it.mediaItemIndex]
                } ?: Media()
            } else Media()
        }
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
            resizeMode = preferences.aspectRatio.toResizeMode()
        )
        MediaGestures(
            mediaState = mediaState,
            controller = controller,
            brightnessState = brightnessController,
        )
        mediaState.playerState?.let {
            Subtitles(
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
            showDialog = viewModel::showDialog,
            switchAspectRatio = viewModel::switchAspectRatio
        )
        if (playerViewState.showDialog == Dialog.AudioTrack) {
            mediaState.playerState?.let { state ->
                TrackSelectorDialog(
                    title = { Text(text = "Select audio track") },
                    onDismiss = { viewModel.showDialog(Dialog.None) },
                    tracks = state.audioTracks,
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.player?.switchTrack(it)
                        }
                    }
                )
            }
        }

        if (playerViewState.showDialog == Dialog.SubtitleTrack) {
            mediaState.playerState?.let { state ->
                TrackSelectorDialog(
                    title = { Text(text = "Select subtitle track") },
                    onDismiss = { viewModel.showDialog(Dialog.None) },
                    tracks = state.subtitleTracks,
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.player?.switchTrack(it)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Subtitles(
    cueGroup: CueGroup,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        cueGroup.cues.forEach {
            Text(
                text = it.text.toString(),
                textAlign = TextAlign.Center
            )
            it.textAlignment
        }
    }
}

@Composable
private fun MediaPlayer(
    state: MediaState,
    modifier: Modifier = Modifier,
    resizeMode: ResizeMode = ResizeMode.Fit
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


private fun ExoPlayer.getTrackGroupFromFormatId(trackType: Int, id: String): Tracks.Group? {
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

private fun AspectRatio.toResizeMode(): ResizeMode {
    return when (this) {
        AspectRatio.FitScreen -> ResizeMode.Fit
        AspectRatio.FixedWidth -> ResizeMode.FixedWidth
        AspectRatio.FixedHeight -> ResizeMode.FixedHeight
        AspectRatio.Fill -> ResizeMode.Fill
        AspectRatio.Zoom -> ResizeMode.Zoom
    }
}