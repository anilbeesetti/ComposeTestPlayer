package com.arcticoss.nextplayer.player.ui.playerscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

private const val TAG = "NextPlayerViewModel"

class NextPlayerViewModel: ViewModel() {

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _lastPlayedPosition = MutableStateFlow(0L)
    val lastPlayedPosition = _lastPlayedPosition.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()


    fun setLastPlayingPosition(millis: Long) {
        _lastPlayedPosition.value = millis
    }

    fun setIsPlaying(state: Boolean) {
        _isPlaying.value = state
    }

    fun setDuration(millis: Long) {
        _duration.value = millis
    }

    fun setCurrentPosition(millis: Long) {
        _currentPosition.value = millis
    }
}