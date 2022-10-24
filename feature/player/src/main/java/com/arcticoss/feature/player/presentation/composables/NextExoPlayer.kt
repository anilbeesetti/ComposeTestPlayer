package com.arcticoss.feature.player.presentation.composables

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.PlayerViewModel
import com.arcticoss.model.AspectRatio
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private const val TAG = "NextExoPlayer"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextExoPlayer(
    onBackPressed: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
    changeOrientation: (requestedOrientation: Int) -> Unit
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val exoPlayer = viewModel.player

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateCurrentPosition(exoPlayer.currentPosition)
            delay(1.seconds / 30)
        }
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    AddLifecycleEventObserver(
        lifecycleOwner = lifecycleOwner,
        onLifecycleEvent = { event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.updatePlayWhenReady(exoPlayer.playWhenReady)
                    exoPlayer.playWhenReady = false
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = playerState.playWhenReady
                }
                else -> {}
            }
        }
    )
    var playerView: StyledPlayerView? = null
    lateinit var playbackStateListener: Player.Listener
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        DisposableEffect(
            AndroidView(
                factory = { androidContext ->
                    StyledPlayerView(androidContext).apply {
                        hideController()
                        useController = false
                        player = exoPlayer
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(Color.Black),
                update = {
                    playerView = it
                }
            )
        ) {
            playbackStateListener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            Log.d(TAG, "onPlaybackStateChanged: buffering")
                        }
                        Player.STATE_ENDED -> {
                            if (!exoPlayer.hasNextMediaItem()) run {
                                onBackPressed()
                            }
                            Log.d(TAG, "onPlaybackStateChanged: ended")
                        }
                        Player.STATE_IDLE -> {
                            Log.d(TAG, "onPlaybackStateChanged: idle")
                        }
                        Player.STATE_READY -> {
                            viewModel.setDuration(exoPlayer.duration)
                        }
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    tracks.groups.forEach {
                        if (it.type == C.TRACK_TYPE_VIDEO) {
                            for (i in 0 until it.length) {
                                val trackFormat = it.getTrackFormat(i)
                                if (trackFormat.height >= trackFormat.width) {
                                    changeOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                } else {
                                    if (trackFormat.rotationDegrees < 90) {
                                        changeOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                                    } else {
                                        changeOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                    }
                                }
                            }
                        }
                    }
                    super.onTracksChanged(tracks)
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    viewModel.setVideoSize(videoSize.width, videoSize.height)
                    super.onVideoSizeChanged(videoSize)
                }
                
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    viewModel.updatePlayWhenReady(exoPlayer.playWhenReady)
                    viewModel.updatePlayingState(isPlaying)
                    playerView?.keepScreenOn = isPlaying
                }
            }
            exoPlayer.addListener(playbackStateListener)
            onDispose {
                exoPlayer.removeListener(playbackStateListener)
                exoPlayer.release()
            }
        }
    }
    LaunchedEffect(key1 = preferences.aspectRatio, key2 = playerState.width) {
        Log.d(TAG, "NextExoPlayer: ${preferences.aspectRatio}")
        when(preferences.aspectRatio) {
            AspectRatio.FitScreen -> {
                playerView?.let {
                    it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
            AspectRatio.Stretch -> {
                playerView?.let {
                    it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            }
            AspectRatio.Crop -> {
                playerView?.let {
                    it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }
        }
    }
}

