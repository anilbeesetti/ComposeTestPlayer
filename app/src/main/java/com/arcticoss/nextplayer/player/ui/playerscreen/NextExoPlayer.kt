package com.arcticoss.nextplayer.player.ui.playerscreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextExoPlayer(
    exoPlayer: ExoPlayer,
    mediaPath: String,
    viewModel: NextPlayerViewModel,
    onVisibilityChange: (visibility: Int) -> Unit
) {
    val lastPlayedPosition by viewModel.lastPlayedPosition.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    LaunchedEffect(exoPlayer) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(mediaPath)))
        exoPlayer.addMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.seekTo(lastPlayedPosition)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    AddLifecycleEventObserver(
        lifecycleOwner = lifecycleOwner,
        onLifecycleEvent = { event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.setIsPlaying(exoPlayer.playWhenReady)
                    exoPlayer.playWhenReady = false
                    viewModel.setLastPlayingPosition(exoPlayer.currentPosition)
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = isPlaying
                }
                Lifecycle.Event.ON_START -> { }
                else -> {}
            }
        }
    )


    lateinit var playerView: StyledPlayerView
    DisposableEffect(
        AndroidView(
            factory = { context ->
                playerView = StyledPlayerView(context).apply {
                    hideController()
                    useController = false
                    player = exoPlayer
                    setControllerVisibilityListener(
                        StyledPlayerView.ControllerVisibilityListener { onVisibilityChange(it) }
                    )
                }
                playerView
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}