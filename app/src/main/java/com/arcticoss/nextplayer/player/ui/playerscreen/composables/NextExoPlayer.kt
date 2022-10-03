package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.player.ui.playerscreen.NextPlayerViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay
import java.io.File
import kotlin.time.Duration.Companion.seconds

private const val TAG = "NextExoPlayer"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextExoPlayer(
    exoPlayer: ExoPlayer,
    mediaPath: String,
    viewModel: NextPlayerViewModel,
    onBackPressed: () -> Unit,
    changeOrientation: (requestedOrientation: Int) -> Unit
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(exoPlayer) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(mediaPath)))
        exoPlayer.addMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        exoPlayer.seekTo(playerState.currentPosition)
    }

    if (playerState.isPlaying) {
        LaunchedEffect(Unit) {
            while (true) {
                viewModel.updateCurrentPosition(exoPlayer.currentPosition)
                delay(1.seconds / 30)
            }
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
                Lifecycle.Event.ON_START -> {}
                else -> {}
            }
        }
    )


    lateinit var playerView: StyledPlayerView
    lateinit var playbackStateListener: Player.Listener
    DisposableEffect(
        AndroidView(
            factory = { androidContext ->
                playerView = StyledPlayerView(androidContext).apply {
                    hideController()
                    useController = false
                    player = exoPlayer
                }
                playerView
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
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
                        Log.d(TAG, "onPlaybackStateChanged: ready")
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

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                viewModel.updatePlayWhenReady(exoPlayer.playWhenReady)
                viewModel.updatePlayingState(isPlaying)
                playerView.keepScreenOn = isPlaying
            }
        }
        exoPlayer.addListener(playbackStateListener)
        onDispose {
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
    }
}

