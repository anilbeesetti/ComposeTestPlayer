package com.arcticoss.nextplayer.feature.player.state

import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.exoplayer2.Player

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
     * The current lock state of controller
     */
    var isControllerLocked: Boolean by mutableStateOf(false)


    /**
     * The current [visibility][ControllerBar] of the controller.
     */
    var controllerBar: ControllerBar by mutableStateOf(ControllerBar.None)

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

    private var controllerAutoShow: Boolean by mutableStateOf(true)

    fun toggleControllerLock() {
        isControllerLocked = !isControllerLocked
    }

    // internally used properties and functions
    private val listener = object : Player.Listener {
        // PlayerListener
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

enum class ControllerBar {
    Volume,
    Brightness,
    None
}
