package com.arcticoss.feature.player

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.model.PlayerUiPreferences
import com.arcticoss.nextplayer.core.datastore.datasource.PlayerPreferencesDataSource
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NextPlayerViewModel"

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val playerPreferencesDataSource: PlayerPreferencesDataSource,
    val player: Player
) : ViewModel() {

    val uiPreferencesFlow = playerPreferencesDataSource.uiPrefStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerUiPreferences()
        )

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

    fun onUiEvent(event: PlayerUiEvent) {
        when (event) {
            is PlayerUiEvent.ShowUi -> _playerUiState.value =
                playerUiState.value.copy(showUi = event.value)
            is PlayerUiEvent.ShowVolumeBar -> _playerUiState.value =
                playerUiState.value.copy(showVolumeBar = event.value)
            is PlayerUiEvent.ShowBrightnessBar -> _playerUiState.value =
                playerUiState.value.copy(showBrightnessBar = event.value)
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.ChangeOrientation -> _playerState.value =
                playerState.value.copy(screenOrientation = event.value)
            is PlayerEvent.SetVolume -> _playerState.value =
                playerState.value.copy(volumeLevel = event.value)
            is PlayerEvent.SetBrightness -> {
                viewModelScope.launch {
                    playerPreferencesDataSource.updateUiPref(
                        uiPreferencesFlow.value.copy(brightnessLevel = event.value)
                    )
                }
            }
        }
    }
}

data class PlayerState(
    val currentPosition: Long = 0,
    val currentMediaItemDuration: Long = 0,
    val currentBrightness: Int = 5,
    val volumeLevel: Int = 0,
    val screenOrientation: Int = 1,
    val isPlaying: Boolean = true,
    val playWhenReady: Boolean = true,
    val maxLevel: Int = 25
)


data class PlayerUiState(
    val showUi: Boolean = false,
    val showBrightnessBar: Boolean = false,
    val showVolumeBar: Boolean = false,
)

sealed class PlayerUiEvent {
    data class ShowUi(val value: Boolean) : PlayerUiEvent()
    data class ShowBrightnessBar(val value: Boolean) : PlayerUiEvent()
    data class ShowVolumeBar(val value: Boolean) : PlayerUiEvent()
}

sealed interface PlayerEvent {
    data class SetVolume(val value: Int) : PlayerEvent
    data class SetBrightness(val value: Int) : PlayerEvent
    data class ChangeOrientation(val value: Int) : PlayerEvent
}

enum class Bar {
    Brightness, Volume
}