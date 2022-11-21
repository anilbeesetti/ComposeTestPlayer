package com.arcticoss.nextplayer.feature.player.compose

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.feature.player.*
import com.arcticoss.nextplayer.feature.player.presentation.isPortrait
import com.arcticoss.nextplayer.feature.player.presentation.rememberControllerState
import com.arcticoss.nextplayer.feature.player.presentation.rememberMediaState
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import java.util.*


private const val TAG = "VideoScreen"

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun VideoScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val mediaState = rememberMediaState(player = viewModel.playerHelper.exoPlayer)
    val controller = rememberControllerState(mediaState = mediaState)
    val playerViewState by viewModel.playerViewState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(key1 = Unit) {
        mediaState.player?.play()
    }

    LaunchedEffect(key1 = mediaState.playerState?.videoFormat) {
        mediaState.playerState?.videoFormat?.let {
            if (it.isPortrait) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
        }
    }

    LaunchedEffect(key1 = mediaState.playerState?.isPlaying) {
        if (mediaState.playerState?.isPlaying == true) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
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

    Box(modifier = Modifier.fillMaxSize()) {
        DisposableEffect(
            mediaState.player?.let {
                VideoSurface(
                    player = it,
                    surfaceType = SurfaceType.SurfaceView,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        ) {
            onDispose { }
        }
        MediaGestures(
            mediaState = mediaState,
            controller = controller
        )
        MediaControls(
            mediaState = mediaState,
            currentMedia = currentMedia,
            controller = controller,
            showDialog = viewModel::showDialog
        )
        if (playerViewState.showDialog == Dialog.AudioTrack) {
            mediaState.playerState?.let { state ->
                AudioTrackSelectorDialog(
                    onDismiss = { viewModel.showDialog(Dialog.None) },
                    tracks = state.audioTracks,
                    onTrackClick = {
                        if (!it.isSelected && it.isSupported) {
                            mediaState.player?.switchAudioTrack(it)
                        }
                    }
                )
            }
        }
    }
}


private fun Player.switchAudioTrack(trackGroup: Tracks.Group) {
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