package com.arcticoss.feature.player.presentation.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.arcticoss.feature.player.PlayerEvent
import com.arcticoss.feature.player.PlayerState
import com.arcticoss.feature.player.PlayerUiState
import com.arcticoss.feature.player.UiEvent
import com.arcticoss.feature.player.utils.*
import kotlinx.coroutines.delay

@Composable
fun EventHandler(
    playerState: PlayerState,
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

    LaunchedEffect(playerState.brightness) {
        val level = 1.0f / playerState.maxLevel * playerState.brightness
        activity?.setBrightness(level)
    }

    LaunchedEffect(playerState.volume) {
        audioManager.setVolume(playerState.volume)
    }

    LaunchedEffect(playerUiState.isControllerVisible, playerState.isPlaying) {
        if (playerUiState.isControllerVisible) {
            activity?.showSystemBars()
            if(playerState.isPlaying) {
                delay(5000)
                onUiEvent(UiEvent.ToggleShowUi)
            }
        } else {
            activity?.hideSystemBars()
        }
    }
}