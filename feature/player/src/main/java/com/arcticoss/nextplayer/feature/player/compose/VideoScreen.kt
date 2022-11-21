package com.arcticoss.nextplayer.feature.player.compose

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.player.PlayerViewModel
import com.arcticoss.nextplayer.feature.player.presentation.composables.SurfaceType
import com.arcticoss.nextplayer.feature.player.presentation.composables.VideoSurface
import com.arcticoss.nextplayer.feature.player.presentation.isPortrait
import com.arcticoss.nextplayer.feature.player.presentation.rememberMediaState
import com.arcticoss.nextplayer.feature.player.utils.findActivity


private const val TAG = "VideoScreen"

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun VideoScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {

    val mediaState = rememberMediaState(player = viewModel.playerHelper.exoPlayer)
    val exoplayerState by viewModel.exoplayerStateStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(key1 = mediaState.playerState?.videoFormat) {
        mediaState.playerState?.videoFormat?.let {
            if (it.isPortrait) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
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
        MediaControls(mediaState = mediaState)
    }
}

