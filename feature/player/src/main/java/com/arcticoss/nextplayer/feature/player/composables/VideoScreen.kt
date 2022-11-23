package com.arcticoss.nextplayer.feature.player.composables

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
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
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.ui.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.player.*
import com.arcticoss.nextplayer.feature.player.presentation.aspectRatio
import com.arcticoss.nextplayer.feature.player.presentation.isPortrait
import com.arcticoss.nextplayer.feature.player.presentation.rememberMediaState
import com.arcticoss.nextplayer.feature.player.state.rememberControllerState
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.keepScreenOn
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val activity = context.findActivity()


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
     * Sets [MediaItem] list to player
     */
    LaunchedEffect(player, playerViewState.mediaList.size) {
        player?.run {
            val mediaItems = playerViewState.mediaList.map {
                MediaItem.Builder()
                    .setUri(File(it.path).toUri())
                    .setMediaId(it.id.toString())
                    .setTag(it)
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
                viewModel.saveState(playerState.mediaItemIndex, controller.positionMs, playerState.playWhenReady)
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
                            viewModel.saveState(it.mediaItemIndex, 0, player?.playWhenReady == true)
                        } else {
                            viewModel.saveState(it.mediaItemIndex, it.positionMs, player?.playWhenReady == true)
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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .run {
                    val aspectRatio = mediaState.playerState?.videoSize?.aspectRatio ?: 0F
                    if (aspectRatio <= 0) fillMaxSize()
                    else resize(aspectRatio, ResizeMode.Fit)
                }
        ) {
            mediaState.player?.let {
                ExoPlayerView(
                    player = it,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        MediaGestures(
            mediaState = mediaState,
            controller = controller
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            mediaState.playerState?.cueGroup?.let { cueGroup ->
                cueGroup.cues.forEach {
                    Text(
                        text = it.text.toString(),
                        textAlign = TextAlign.Center
                    )
                    it.textAlignment
                }
            }
        }
        MediaControls(
            mediaState = mediaState,
            currentMedia = currentMedia,
            controller = controller,
            showDialog = viewModel::showDialog
        )
        if (playerViewState.showDialog == Dialog.AudioTrack) {
            mediaState.playerState?.let { state ->
                TrackSelectorDialog(
                    title = {Text(text = "Select audio track")},
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
                    title = {Text(text = "Select subtitle track")},
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