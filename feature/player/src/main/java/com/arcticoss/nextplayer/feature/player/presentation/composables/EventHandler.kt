package com.arcticoss.nextplayer.feature.player.presentation.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.arcticoss.nextplayer.feature.player.PlayerEvent
import com.arcticoss.nextplayer.feature.player.ExoplayerState
import com.arcticoss.nextplayer.feature.player.PlayerUiState
import com.arcticoss.nextplayer.feature.player.UiEvent
import com.arcticoss.nextplayer.feature.player.utils.*
import kotlinx.coroutines.delay

@Composable
fun EventHandler(
    exoPlayerState: ExoplayerState,
    playerUiState: PlayerUiState,
    onUiEvent: (UiEvent) -> Unit,
    onEvent: (PlayerEvent) -> Unit,
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context.findActivity()

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            onEvent(PlayerEvent.SetVolume(audioManager.getVolume()))
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            onUiEvent(UiEvent.SavePlaybackState)
        }
    }

    LaunchedEffect(exoPlayerState.brightness) {
        val level = 1.0f / exoPlayerState.maxLevel * exoPlayerState.brightness
        activity?.setBrightness(level)
    }

    LaunchedEffect(exoPlayerState.volume) {
        audioManager.setVolume(exoPlayerState.volume)
    }

    LaunchedEffect(playerUiState.isControllerVisible, exoPlayerState.isPlaying) {
        if (playerUiState.isControllerVisible) {
            activity?.showSystemBars()
            if(exoPlayerState.isPlaying) {
                delay(5000)
                onUiEvent(UiEvent.ToggleShowUi)
            }
        } else {
            activity?.hideSystemBars()
        }
    }
}