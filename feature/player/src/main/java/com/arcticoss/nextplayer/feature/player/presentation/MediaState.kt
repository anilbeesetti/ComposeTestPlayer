package com.arcticoss.nextplayer.feature.player.presentation

import android.os.Looper
import androidx.compose.runtime.*
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import kotlin.math.absoluteValue

@Composable
fun rememberMediaState(
    player: Player?
): MediaState = remember { MediaState(initPlayer = player) }.apply {
    this.player = player
}


@Stable
class MediaState(
    initPlayer: Player? = null
) {
    /**
     * The player to use, or null to detach the current player.
     * Only players which are accessed on the main thread are supported (`
     * player.getApplicationLooper() == Looper.getMainLooper()`).
     */
    var player: Player?
        set(current) {
            require(current == null || current.applicationLooper == Looper.getMainLooper()) {
                "Only players which are accessed on the main thread are supported."
            }
            val previous = _player
            if (current !== previous) {
                _player = current
                onPlayerChanged(previous, current)
            }
        }
        get() = _player

    /**
     * The state of the [player].
     */
    val playerState: PlayerState? get() = stateOfPlayerState.value

    // Controller visibility related properties and functions
    /**
     * Whether the controller is showing.
     */
    var isControllerShowing: Boolean
        get() = controllerVisibility.isShowing
        set(value) {
            controllerVisibility = if (value) ControllerVisibility.Visible
            else ControllerVisibility.Invisible
        }

    /**
     * The current [visibility][ControllerVisibility] of the controller.
     */
    var controllerVisibility: ControllerVisibility by mutableStateOf(ControllerVisibility.Invisible)

    /**
     * Typically, when controller is shown, it will be automatically hidden after a short time has
     * elapsed without user interaction. If [shouldShowControllerIndefinitely] is true, you should
     * consider disabling this behavior, and show the controller indefinitely.
     */
    val shouldShowControllerIndefinitely: Boolean by derivedStateOf {
        playerState?.run {
            controllerAutoShow
                    && !timeline.isEmpty
                    && (playbackState == Player.STATE_IDLE
                    || playbackState == Player.STATE_ENDED
                    || !playWhenReady)
        } ?: true
    }

    internal var controllerAutoShow: Boolean by mutableStateOf(true)

    internal fun maybeShowController() {
        if (shouldShowControllerIndefinitely) {
            controllerVisibility = ControllerVisibility.Visible
        }
    }

    // internally used properties and functions
    private val listener = object : Player.Listener {
        override fun onRenderedFirstFrame() {

        }
    }
    private var _player: Player? by mutableStateOf(initPlayer)
    private fun onPlayerChanged(previous: Player?, current: Player?) {
        previous?.removeListener(listener)
        stateOfPlayerState.value?.dispose()
        stateOfPlayerState.value = current?.state()
        current?.addListener(listener)
        if (current == null) {
            controllerVisibility = ControllerVisibility.Invisible
        }
    }

    internal val stateOfPlayerState = mutableStateOf(initPlayer?.state())


    private var _contentAspectRatio by mutableStateOf(0f)
    internal var contentAspectRatio
        internal set(value) {
            val aspectDeformation: Float = value / contentAspectRatio - 1f
            if (aspectDeformation.absoluteValue > 0.01f) {
                // Not within the allowed tolerance, populate the new aspectRatio.
                _contentAspectRatio = value
            }
        }
        get() = _contentAspectRatio

    internal val playerError: PlaybackException? by derivedStateOf {
        playerState?.playerError
    }

    init {
        initPlayer?.addListener(listener)
    }
}

/**
 * The visibility state of the controller.
 */
enum class ControllerVisibility(
    val isShowing: Boolean,
) {
    /**
     * All UI controls are visible.
     */
    Visible(true),

    /**
     * A part of UI controls are visible.
     */
    PartiallyVisible(true),

    /**
     * All UI controls are hidden.
     */
    Invisible(false)
}

val VideoSize.aspectRatio
    get() = if (height == 0) 0f else width * pixelWidthHeightRatio / height

val Format.isPortrait: Boolean
    get() {
        val isRotated = this.rotationDegrees == 90 || this.rotationDegrees == 270
        return if (isRotated) {
            this.width > this.height
        } else {
            this.height > this.width
        }
    }
