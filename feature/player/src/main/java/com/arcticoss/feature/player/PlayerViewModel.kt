package com.arcticoss.feature.player

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.feature.player.utils.Orientation
import com.arcticoss.model.PlayerPreferences
import com.arcticoss.nextplayer.core.datastore.datasource.PlayerPreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetSortedMediaFolderStreamUseCase
import com.arcticoss.nextplayer.core.domain.GetSortedMediaItemsStreamUseCase
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val preferencesDataSource: PlayerPreferencesDataSource,
    getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase,
    getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase,
    normalPlayer: Player
) : ViewModel() {

    private val mediaID = savedStateHandle.get<Long>("mediaID")
    private val folderID = savedStateHandle.get<Long>("folderID")

    val player = normalPlayer as ExoPlayer

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState = _playerUiState.asStateFlow()

    val playerCurrentPosition = getPlayerCurrentPosition()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0L
        )

    val preferencesFlow = preferencesDataSource.preferencesFlow
        .onEach { preferences ->
            if (preferences.saveBrightnessLevel) {
                _playerState.update { it.copy(brightness = preferences.brightnessLevel) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerPreferences()
        )

    private fun getPlayerCurrentPosition() = flow {
        while (true) {
            emit(player.currentPosition)
            delay(1.seconds / 30)
        }
    }

    fun addVideoUri(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    init {
        folderID?.let { id ->
            if (id == 0L) {
                getSortedMediaItemsStream().onEach { mediaItemList ->
                    val index = mediaItemList.indexOfFirst { it.id == mediaID }
                    val mediaItems = mediaItemList.map {
                        MediaItem.fromUri(File(it.path).toUri())
                    }
                    player.setMediaItems(mediaItems)
                    for (i in 0 until index) {
                        player.seekToNextMediaItem()
                    }
                    player.prepare()
                }.launchIn(viewModelScope)
            } else {
                getSortedMediaFolderStream(id).onEach { folder ->
                    val index = folder.mediaItems.indexOfFirst { it.id == mediaID }
                    val mediaItems = folder.mediaItems.map {
                        MediaItem.fromUri(File(it.path).toUri())
                    }
                    player.setMediaItems(mediaItems)
                    for (i in 0 until index) {
                        player.seekToNextMediaItem()
                    }
                    player.prepare()
                }.launchIn(viewModelScope)
            }
        }
    }

    fun onUiEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ShowUi -> _playerUiState.update {
                it.copy(isControllerVisible = event.value)
            }
            is UiEvent.ShowVolumeBar -> _playerUiState.update {
                it.copy(isVolumeBarVisible = event.value)
            }
            is UiEvent.ShowBrightnessBar -> _playerUiState.update {
                it.copy(isBrightnessBarVisible = event.value)
            }
            is UiEvent.ShowSeekBar -> _playerUiState.update {
                it.copy(isSeekBarVisible = event.value)
            }
            UiEvent.ToggleShowUi -> _playerUiState.update {
                it.copy(isControllerVisible = !it.isControllerVisible)
            }
            UiEvent.ToggleAspectRatio -> viewModelScope.launch {
                preferencesDataSource.switchAspectRatio()
            }
        }
    }

    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.SetOrientation -> _playerState.update {
                it.copy(screenOrientation = event.value)
            }
            is PlayerEvent.SetVolume -> _playerState.update {
                it.copy(volume = event.value)
            }
            is PlayerEvent.SetDuration -> _playerState.update {
                it.copy(currentMediaItemDuration = event.value)
            }
            is PlayerEvent.SetPlaybackState -> _playerState.update {
                it.copy(isPlaying = event.value)
            }
            is PlayerEvent.SetPlayWhenReady -> _playerState.update {
                it.copy(playWhenReady = event.value)
            }
            is PlayerEvent.SetCurrentPosition -> _playerState.update {
                it.copy(currentPosition = event.value)
            }
            PlayerEvent.IncreaseVolume -> {
                val volume = playerState.value.volume
                if (volume < 25) {
                    _playerState.update { it.copy(volume = volume + 1) }
                }
            }
            PlayerEvent.DecreaseVolume -> {
                val volume = playerState.value.volume
                if (volume > 0) {
                    _playerState.update { it.copy(volume = volume - 1) }
                }
            }
            PlayerEvent.IncreaseBrightness -> {
                val brightness = playerState.value.brightness
                if (brightness < 25) {
                    if (preferencesFlow.value.saveBrightnessLevel) {
                        viewModelScope.launch {
                            preferencesDataSource.updateBrightnessLevel(brightness + 1)
                        }
                    } else {
                        _playerState.update { it.copy(brightness = brightness + 1) }
                    }
                }
            }
            PlayerEvent.DecreaseBrightness -> {
                val brightness = playerState.value.brightness
                if (brightness > 0) {
                    if (preferencesFlow.value.saveBrightnessLevel) {
                        viewModelScope.launch {
                            preferencesDataSource.updateBrightnessLevel(brightness - 1)
                        }
                    } else {
                        _playerState.update { it.copy(brightness = brightness - 1) }
                    }
                }
            }
        }
    }
}

data class PlayerState(
    val width: Int = 0,
    val volume: Int = 0,
    val height: Int = 0,
    val maxLevel: Int = 25,
    val brightness: Int = 5,
    val isPlaying: Boolean = true,
    val currentPosition: Long = 0,
    val playWhenReady: Boolean = true,
    val currentMediaItemDuration: Long = 0,
    val screenOrientation: Orientation = Orientation.PORTRAIT
)


data class PlayerUiState(
    val isSeekBarVisible: Boolean = false,
    val isVolumeBarVisible: Boolean = false,
    val isControllerVisible: Boolean = false,
    val isBrightnessBarVisible: Boolean = false,
)

sealed interface UiEvent {
    object ToggleShowUi: UiEvent
    object ToggleAspectRatio: UiEvent
    data class ShowUi(val value: Boolean) : UiEvent
    data class ShowSeekBar(val value: Boolean) : UiEvent
    data class ShowVolumeBar(val value: Boolean) : UiEvent
    data class ShowBrightnessBar(val value: Boolean) : UiEvent
}

sealed interface PlayerEvent {
    object IncreaseVolume: PlayerEvent
    object DecreaseVolume: PlayerEvent
    object IncreaseBrightness: PlayerEvent
    object DecreaseBrightness: PlayerEvent
    data class SetVolume(val value: Int) : PlayerEvent
    data class SetDuration(val value: Long): PlayerEvent
    data class SetCurrentPosition(val value: Long): PlayerEvent
    data class SetPlaybackState(val value: Boolean): PlayerEvent
    data class SetPlayWhenReady(val value: Boolean): PlayerEvent
    data class SetOrientation(val value: Orientation) : PlayerEvent
}