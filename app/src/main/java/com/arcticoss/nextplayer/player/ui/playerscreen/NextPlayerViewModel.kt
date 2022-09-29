package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class NextPlayerViewModel: ViewModel() {

    private val _lastPlayedPosition = MutableStateFlow(0L)
    val lastPlayedPosition = _lastPlayedPosition.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying = _isPlaying.asStateFlow()


    fun setLastPlayingPosition(millis: Long) {
        _lastPlayedPosition.value = millis
    }

    fun setIsPlaying(state: Boolean) {
        _isPlaying.value = state
    }
}