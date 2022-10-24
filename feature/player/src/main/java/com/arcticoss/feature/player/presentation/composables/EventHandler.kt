package com.arcticoss.feature.player.presentation.composables

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.*
import com.arcticoss.feature.player.utils.findActivity
import com.arcticoss.feature.player.utils.setBrightness
import com.arcticoss.feature.player.utils.setVolume
import kotlinx.coroutines.delay

private const val DELAY = 1000L

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EventHandler(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val lifecycleOwner = LocalLifecycleOwner.current

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            viewModel.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
        }
    }


    LaunchedEffect(playerState.brightnessLevel) {
        val activity = context.findActivity()
        val level = 1.0f / playerState.maxLevel * playerState.brightnessLevel
        activity?.setBrightness(level)
        delay(DELAY)
        viewModel.hideBrightnessBar()
    }

    LaunchedEffect(playerState.volumeLevel) {
        audioManager.setVolume(playerState.volumeLevel)
        delay(DELAY)
        viewModel.hideVolumeBar()
    }
}