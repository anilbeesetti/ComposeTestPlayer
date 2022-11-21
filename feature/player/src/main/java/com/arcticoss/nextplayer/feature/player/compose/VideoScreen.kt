package com.arcticoss.nextplayer.feature.player.compose

import android.annotation.SuppressLint
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

