package com.arcticoss.feature.player

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.model.PlayerPreferences
import com.arcticoss.nextplayer.core.datastore.datasource.PlayerPreferencesDataSource
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NextPlayerViewModel"

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferencesDataSource: PlayerPreferencesDataSource,
    normalPlayer: Player
) : ViewModel() {

    val player = normalPlayer as ExoPlayer

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    val preferencesFlow = preferencesDataSource.preferencesFlow
        .onEach {
            if (it.saveBrightnessLevel) {
                _playerState.value = _playerState.value.copy(brightnessLevel = it.brightnessLevel)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerPreferences()
        )

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState = _playerUiState.asStateFlow()

    fun addVideoUri(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    fun seekTo(positionMs: Long) {
        if (preferencesFlow.value.fastSeeking) {
            player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
        } else {
            player.setSeekParameters(SeekParameters.DEFAULT)
        }
        player.seekTo(positionMs)
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
            is PlayerUiEvent.ShowUi -> _playerUiState.update {
                it.copy(isControllerVisible = event.value)
            }
            is PlayerUiEvent.ShowVolumeBar -> _playerUiState.update {
                it.copy(isVolumeBarVisible = event.value)
            }
            is PlayerUiEvent.ShowBrightnessBar -> _playerUiState.update {
                it.copy(isBrightnessBarVisible = event.value)
            }
            is PlayerUiEvent.ShowSeekBar -> _playerUiState.update {
                it.copy(isSeekBarVisible = event.value)
            }
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.ChangeOrientation ->
                _playerState.value = playerState.value.copy(screenOrientation = event.value)
            is PlayerEvent.SetVolume ->
                _playerState.value = playerState.value.copy(volumeLevel = event.value)
            is PlayerEvent.SetBrightness -> {
                if (preferencesFlow.value.saveBrightnessLevel) {
                    viewModelScope.launch { preferencesDataSource.updateBrightnessLevel(event.value) }
                } else {
                    _playerState.value = playerState.value.copy(brightnessLevel = event.value)
                }
            }
        }
    }

    fun switchAspectRatio() {
        viewModelScope.launch { preferencesDataSource.switchAspectRatio() }
    }

    fun setVideoSize(width: Int, height: Int) {
        _playerState.value = _playerState.value.copy(width = width, height = height)
    }
}

data class PlayerState(
    val width: Int = 0,
    val height: Int = 0,
    val currentPosition: Long = 0,
    val currentMediaItemDuration: Long = 0,
    val brightnessLevel: Int = 5,
    val volumeLevel: Int = 0,
    val screenOrientation: Int = 1,
    val isPlaying: Boolean = true,
    val playWhenReady: Boolean = true,
    val maxLevel: Int = 25
)


data class PlayerUiState(
    val isControllerVisible: Boolean = false,
    val isBrightnessBarVisible: Boolean = false,
    val isVolumeBarVisible: Boolean = false,
    val isSeekBarVisible: Boolean = false,
)

sealed class PlayerUiEvent {
    data class ShowUi(val value: Boolean) : PlayerUiEvent()
    data class ShowBrightnessBar(val value: Boolean) : PlayerUiEvent()
    data class ShowVolumeBar(val value: Boolean) : PlayerUiEvent()
    data class ShowSeekBar(val value: Boolean) : PlayerUiEvent()
}

sealed interface PlayerEvent {
    data class SetVolume(val value: Int) : PlayerEvent
    data class SetBrightness(val value: Int) : PlayerEvent
    data class ChangeOrientation(val value: Int) : PlayerEvent
}

enum class Bar {
    Brightness, Volume
}