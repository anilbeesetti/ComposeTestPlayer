package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "NextPlayerViewModel"

class NextPlayerViewModel : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()


    fun updatePlayingState(isPlaying: Boolean) {
        _playerState.value = playerState.value.copy(
            isPlaying = isPlaying
        )
    }

    fun updateCurrentPosition(currentPosition: Long) {
        _playerState.value = playerState.value.copy(
            currentPosition = currentPosition
        )
    }

    fun updatePlayWhenReady(playWhenReady: Boolean) {
        _playerState.value = playerState.value.copy(
            playWhenReady = playWhenReady
        )
    }

    fun setDuration(millis: Long) {
        _playerState.value = playerState.value.copy(
            currentMediaItemDuration = millis
        )
    }

    fun updateBrightness(brightness: Int) {
        _playerState.value = playerState.value.copy(
            currentBrightness = brightness
        )
    }

    fun updateScreenOrientation(currentOrientation: Int) {
        _playerState.value = playerState.value.copy(
            screenOrientation = currentOrientation
        )
    }
}

data class PlayerState(
    val currentPosition: Long = 0,
    val currentMediaItemDuration: Long = 0,
    val currentBrightness: Int = 5,
    val screenOrientation: Int = 1,
    val isPlaying: Boolean = true,
    val playWhenReady: Boolean = true
)