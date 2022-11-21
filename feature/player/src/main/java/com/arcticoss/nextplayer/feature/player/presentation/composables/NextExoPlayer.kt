package com.arcticoss.nextplayer.feature.player.presentation.composables

import android.util.Log
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.arcticoss.nextplayer.core.model.AspectRatio
import com.arcticoss.nextplayer.feature.player.AudioTrack
import com.arcticoss.nextplayer.feature.player.PlayerEvent
import com.arcticoss.nextplayer.feature.player.ExoplayerState
import com.arcticoss.nextplayer.feature.player.utils.Orientation
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.setOrientation
import com.google.android.exoplayer2.*
import java.util.*

private const val TAG = "NextExoPlayer"

@Composable
fun NextExoPlayer(
    exoPlayer: ExoPlayer,
    exoPlayerState: ExoplayerState,
    aspectRatio: AspectRatio,
    onEvent: (PlayerEvent) -> Unit,
    onBackPressed: () -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    val lifecycleOwner = LocalLifecycleOwner.current
    AddLifecycleEventObserver(
        lifecycleOwner = lifecycleOwner,
        onLifecycleEvent = { event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "NextExoPlayer: ${exoPlayer.isPlaying}")
                    onEvent(PlayerEvent.SetPlayWhenReady(false))
                    exoPlayer.playWhenReady = false
                    Log.d(TAG, "NextExoPlayer: ${exoPlayerState.playWhenReady}")
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = exoPlayerState.playWhenReady
                    Log.d(TAG, "NextExoPlayer: ${exoPlayerState.playWhenReady}")
                }
                else -> {}
            }
        }
    )

    lateinit var playbackStateListener: Player.Listener
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        DisposableEffect(
            VideoSurface(
                player = exoPlayer,
                surfaceType = SurfaceType.SurfaceView,
                modifier = Modifier.fillMaxSize()
            )
//            AndroidView(
//                factory = { androidContext ->
//                    StyledPlayerView(androidContext).apply {
//                        hideController()
//                        useController = false
//                        player = exoPlayer
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxSize()
//                    .align(Alignment.Center)
//                    .background(Color.Black),
//                update = {
//                    playerView = it
//                }
//            )
        ) {
            playbackStateListener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    onEvent(PlayerEvent.PlaybackState(playbackState))
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
                            onEvent(PlayerEvent.SetDuration(exoPlayer.duration))
                            onEvent(PlayerEvent.PlaybackStarted(true))
                        }
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    val audioTracks = mutableListOf<AudioTrack>()
                    tracks.groups.forEach { trackGroup ->
                        when (trackGroup.type) {
                            C.TRACK_TYPE_VIDEO -> {
                                for (i in 0 until trackGroup.length) {
                                    val trackFormat = trackGroup.getTrackFormat(i)
                                    if (trackFormat.height >= trackFormat.width) {
                                        activity?.setOrientation(Orientation.PORTRAIT) {
                                            onEvent(PlayerEvent.SetOrientation(it))
                                        }
                                    } else {
                                        if (trackFormat.rotationDegrees < 90) {
                                            activity?.setOrientation(Orientation.LANDSCAPE_SENSOR) {
                                                onEvent(PlayerEvent.SetOrientation(it))
                                            }
                                        } else {
                                            activity?.setOrientation(Orientation.PORTRAIT) {
                                                onEvent(PlayerEvent.SetOrientation(it))
                                            }
                                        }
                                    }
                                }
                            }
                            C.TRACK_TYPE_AUDIO -> {
                                for (i in 0 until trackGroup.length) {
                                    val trackFormat = trackGroup.getTrackFormat(i)
                                    var displayName = ""
                                    trackFormat.language?.let {
                                        displayName += if (trackFormat.language != "und") {
                                            Locale(trackFormat.language.toString()).displayLanguage
                                        } else {
                                            trackFormat.sampleMimeType
                                        }
                                    }
                                    trackFormat.label?.let {
                                        displayName += "," + trackFormat.label
                                    }
                                    audioTracks.add(
                                        AudioTrack(
                                            displayName = displayName,
                                            formatId = trackFormat.id.toString(),
                                            isSelected = trackGroup.isSelected
                                        )
                                    )
                                }
                            }
                        }
                    }
                    onEvent(PlayerEvent.AddAudioTracks(audioTracks))
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    onEvent(PlayerEvent.SetIsPlayingState(isPlaying))
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    mediaItem?.let {
                        onEvent(PlayerEvent.MediaItemTransition(it.mediaId.toLong()))
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.d(TAG, "onPlayerError: ${error.cause}")
                    onEvent(PlayerEvent.PlayerError(true))
                }

                override fun onVolumeChanged(volume: Float) {
                    Log.d(TAG, "onVolumeChanged: $volume")
                }

                override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                    Log.d(TAG, "onDeviceVolumeChanged: $volume $muted")
                }

            }
            exoPlayer.addListener(playbackStateListener)
            onDispose {
                exoPlayer.removeListener(playbackStateListener)
                exoPlayer.release()
            }
        }
    }
}


@Composable
fun VideoSurface(
    player: Player,
    surfaceType: SurfaceType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun Player.clearVideoView(view: View) {
        when(surfaceType) {
            SurfaceType.SurfaceView -> clearVideoSurfaceView(view as SurfaceView)
            SurfaceType.TextureView -> clearVideoTextureView(view as TextureView)
        }
    }

    fun Player.setVideoView(view: View) {
        when (surfaceType) {
            SurfaceType.SurfaceView -> setVideoSurfaceView(view as SurfaceView)
            SurfaceType.TextureView -> setVideoTextureView(view as TextureView)
        }
    }

    val videoView = remember {
        when (surfaceType) {
            SurfaceType.SurfaceView -> SurfaceView(context)
            SurfaceType.TextureView -> TextureView(context)
        }
    }

    AndroidView(
        factory = { videoView },
        modifier = modifier
    ) {
        val previousPlayer = it.tag as? Player
        if (previousPlayer == player) return@AndroidView

        previousPlayer?.clearVideoView(it)
        it.tag = player.apply { setVideoView(it) }
    }

    DisposableEffect(Unit) {
        onDispose {
            (videoView.tag as? Player)?.clearVideoView(videoView)
        }
    }
}


enum class SurfaceType {
    SurfaceView,
    TextureView;
}



