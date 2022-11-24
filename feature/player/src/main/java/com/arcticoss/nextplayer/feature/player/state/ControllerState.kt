package com.arcticoss.nextplayer.feature.player.state

import androidx.compose.runtime.*
import com.arcticoss.nextplayer.feature.player.presentation.MediaState
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import kotlinx.coroutines.delay

/**
 * Create and [remember] a [ControllerState] instance.
 */
@Composable
fun rememberControllerState(
    mediaState: MediaState
): ControllerState {
    val controllerState = remember { ControllerState(mediaState) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(200)
            controllerState.triggerPositionUpdate()
        }
    }
    return controllerState
}

/**
 * Create a [ControllerState] instance.
 */
fun ControllerState(mediaState: MediaState): ControllerState {
    return ControllerState(mediaState.stateOfPlayerState)
}

@Stable
class ControllerState internal constructor(
    stateOfPlayerState: State<PlayerState?>
) {
    private val playerState: PlayerState? by stateOfPlayerState
    private val player: Player? get() = playerState?.player

    /**
     * If ture, show pause button. Otherwise, show play button.
     */
    val showPause: Boolean by derivedStateOf {
        playerState?.run {
            playbackState != Player.STATE_ENDED
                    && playbackState != Player.STATE_IDLE
                    && playWhenReady
        } ?: false
    }

    /**
     * Is player currently playing
     */
    val isPlaying: Boolean by derivedStateOf {
        playerState?.isPlaying ?: false
    }

    /**
     * Play or pause the player.
     */
    fun playOrPause() {
        player?.run {
            if (playbackState == Player.STATE_IDLE
                || playbackState == Player.STATE_ENDED
                || !playWhenReady
            ) {
                if (playbackState == Player.STATE_IDLE) {
                    prepare()
                } else if (playbackState == Player.STATE_ENDED) {
                    seekTo(currentMediaItemIndex, C.TIME_UNSET)
                }
                play()
            } else {
                pause()
            }
        }
    }

    /**
     * Sets seek parameters to player
     */
    fun setSeekParameters(seekParameters: SeekParameters) {
        (player as? ExoPlayer)?.setSeekParameters(seekParameters)
    }

    /**
     * Seek to specified position
     */
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }


    /**
     * Play the player
     */
    fun play() {
        player?.play()
    }

    /**
     * Pause the player
     */
    fun pause() {
        player?.pause()
    }


    /**
     * The duration, in milliseconds. Return [C.TIME_UNSET] if it's unset or unknown.
     */
    val durationMs: Long by derivedStateOf {
        positionUpdateTrigger
        playerState?.run { player.contentDuration } ?: 0L
    }

    /**
     * The current position, in milliseconds.
     */
    val positionMs: Long by derivedStateOf {
        positionUpdateTrigger
        playerState?.run { player.contentPosition } ?: 0L
    }

    /**
     * The current buffered position, in milliseconds.
     */
    val bufferedPositionMs: Long by derivedStateOf {
        positionUpdateTrigger
        playerState?.run { player.contentBufferedPosition } ?: 0L
    }


    private var positionUpdateTrigger by mutableStateOf(0L)

    fun triggerPositionUpdate() {
        positionUpdateTrigger++
    }
}