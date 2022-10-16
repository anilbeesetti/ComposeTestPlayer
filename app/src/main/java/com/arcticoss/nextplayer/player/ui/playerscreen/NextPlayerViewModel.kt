package com.arcticoss.nextplayer.player.ui.playerscreen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

private const val TAG = "NextPlayerViewModel"

@HiltViewModel
class NextPlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val player: Player
) : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState = _playerUiState.asStateFlow()

    fun addVideoUri(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

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

    fun updateVolumeLevel(volumeLevel: Int) {
        _playerState.value = playerState.value.copy(
            currentVolumeLevel = volumeLevel
        )
    }

    fun updateScreenOrientation(currentOrientation: Int) {
        _playerState.value = playerState.value.copy(
            screenOrientation = currentOrientation
        )
    }

    fun updatePlayerState(state: PlayerState) {
        _playerState.value = state
    }

    fun onUiEvent(event: PlayerUiEvent) {
        when(event) {
            is PlayerUiEvent.ShowUi -> _playerUiState.value = playerUiState.value.copy(showUi = event.value)
            is PlayerUiEvent.ShowBrightnessBar -> _playerUiState.value = playerUiState.value.copy(showBrightnessBar = event.value)
            is PlayerUiEvent.ShowVolumeBar -> _playerUiState.value = playerUiState.value.copy(showVolumeBar = event.value)
        }
    }
}

data class PlayerState(
    val currentPosition: Long = 0,
    val currentMediaItemDuration: Long = 0,
    val currentBrightness: Int = 5,
    val currentVolumeLevel: Int = 0,
    val screenOrientation: Int = 1,
    val isPlaying: Boolean = true,
    val playWhenReady: Boolean = true
)


data class PlayerUiState(
    val showUi: Boolean = false,
    val showBrightnessBar: Boolean = false,
    val showVolumeBar: Boolean = false
)

sealed class PlayerUiEvent {
    data class ShowUi(val value: Boolean): PlayerUiEvent()
    data class ShowBrightnessBar(val value: Boolean): PlayerUiEvent()
    data class ShowVolumeBar(val value: Boolean): PlayerUiEvent()
}