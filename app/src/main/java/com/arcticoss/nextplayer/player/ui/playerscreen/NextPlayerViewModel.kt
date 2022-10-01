package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "NextPlayerViewModel"

class NextPlayerViewModel : ViewModel() {

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _lastPlayedPosition = MutableStateFlow(0L)
    val lastPlayedPosition = _lastPlayedPosition.asStateFlow()

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

    fun setLastPlayingPosition(millis: Long) {
        _lastPlayedPosition.value = millis
    }

    fun setDuration(millis: Long) {
        _duration.value = millis
    }
}

data class PlayerState(
    val currentPosition: Long = 0,
    val isPlaying: Boolean = true
)