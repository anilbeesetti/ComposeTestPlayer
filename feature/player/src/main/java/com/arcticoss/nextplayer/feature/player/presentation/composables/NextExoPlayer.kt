package com.arcticoss.nextplayer.feature.player.presentation.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.arcticoss.nextplayer.core.model.AspectRatio
import com.arcticoss.nextplayer.feature.player.AudioTrack
import com.arcticoss.nextplayer.feature.player.PlayerEvent
import com.arcticoss.nextplayer.feature.player.utils.Orientation
import com.arcticoss.nextplayer.feature.player.utils.findActivity
import com.arcticoss.nextplayer.feature.player.utils.setOrientation
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.util.*

private const val TAG = "NextExoPlayer"

@Composable
fun NextExoPlayer(
    exoPlayer: ExoPlayer,
    playWhenReady: Boolean,
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
                    onEvent(PlayerEvent.SetPlayWhenReady(exoPlayer.playWhenReady))
                    exoPlayer.playWhenReady = false
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = playWhenReady
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
                    Log.d(TAG, "NextExoPlayer: hie")
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
                            onEvent(PlayerEvent.SetDuration(exoPlayer.duration))
                        }
                    }
                }

                override fun onTracksChanged(tracks: Tracks) {
                    val audioTracks = mutableListOf<AudioTrack>()
                    tracks.groups.forEach { trackGroup ->
                        when(trackGroup.type) {
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
                                            lang = trackFormat.language.toString(),
                                            isSelected = trackGroup.isSelected
                                        )
                                    )
                                }
                            }
                        }
                    }
                    onEvent(PlayerEvent.AddAudioTracks(audioTracks))
                    super.onTracksChanged(tracks)
                }
                
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    onEvent(PlayerEvent.SetPlaybackState(isPlaying))
                    playerView?.keepScreenOn = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    mediaItem?.let {
                        onEvent(PlayerEvent.MediaItemTransition(it.mediaId.toLong()))
                    }
                }

            }
            exoPlayer.addListener(playbackStateListener)
            onDispose {
                exoPlayer.removeListener(playbackStateListener)
                exoPlayer.release()
            }
        }
    }
    LaunchedEffect(key1 = aspectRatio) {
        when(aspectRatio) {
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

