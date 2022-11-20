package com.arcticoss.nextplayer.feature.player

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.data.repository.IMediaRepository
import com.arcticoss.nextplayer.core.datastore.datasource.PlayerPreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetMediaFromUriUseCase
import com.arcticoss.nextplayer.core.domain.GetSortedMediaFolderStreamUseCase
import com.arcticoss.nextplayer.core.domain.GetSortedMediaItemsStreamUseCase
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.Resume
import com.arcticoss.nextplayer.feature.player.utils.Orientation
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaRepository: IMediaRepository,
    private val preferencesDataSource: PlayerPreferencesDataSource,
    private val getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase,
    private val getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase,
    private val getMediaFromUri: GetMediaFromUriUseCase,
    val playerHelper: IPlayerHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaID = savedStateHandle.get<Long>("mediaID")
    private val folderID = savedStateHandle.get<Long>("folderID")

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState = _playerUiState.asStateFlow()

    val playerCurrentPosition = playerHelper.currentPositionFlow
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

    init {
        folderID?.let { id ->
            if (id == 0L) {
                getSortedMediaItemsStream().onEach { mediaList ->
                    setMedia(mediaList)
                }.launchIn(viewModelScope)
            } else {
                getSortedMediaFolderStream(id).onEach { mediaFolder ->
                    setMedia(mediaFolder.mediaList)
                }.launchIn(viewModelScope)
            }
        }
    }

    fun invokeMedia(uri: Uri) {
        viewModelScope.launch {
            val mediaItem = getMediaFromUri(uri)
            mediaItem?.let { media ->
                setMedia(listOf(media))
            }
        }
    }

    private fun setMedia(mediaList: List<Media>) {
        _playerState.update { it.copy(mediaList = mediaList) }
        if (playerState.value.currentPlayingMedia.id == 0L) {
            val index = mediaList.indexOfFirst { it.id == mediaID }
            val mediaItems = playerState.value.mediaList.map {
                MediaItem.Builder().setUri(File(it.path).toUri()).setMediaId(it.id.toString())
                    .build()
            }
            playerHelper.exoPlayer.setMediaItems(mediaItems)
            playerHelper.exoPlayer.prepare()
            playerHelper.moveToMediaItem(index)
        }
    }

    private fun saveMediaState() {
        if (playerState.value.currentPlayingMedia.id != 0L) {
            viewModelScope.launch {
                mediaRepository.updateMedia(
                    playerState.value.currentPlayingMedia.id,
                    lastPlayedPosition = playerCurrentPosition.value
                )
            }
        }
    }

    private fun restoreMediaState() {
        if (preferencesFlow.value.resume == Resume.Always) {
            playerHelper.exoPlayer.seekTo(playerState.value.currentPlayingMedia.lastPlayedPosition)
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
            is UiEvent.ShowAudioTrackDialog -> _playerUiState.update {
                it.copy(isAudioTrackDialogVisible = event.value)
            }
            is UiEvent.SeekToMediaItem -> {
                this.onUiEvent(UiEvent.SavePlaybackState)
                playerHelper.moveToMediaItem(event.value)
            }
            UiEvent.ToggleShowUi -> _playerUiState.update {
                it.copy(isControllerVisible = !it.isControllerVisible)
            }
            UiEvent.ToggleAspectRatio -> viewModelScope.launch {
                preferencesDataSource.switchAspectRatio()
            }
            UiEvent.SavePlaybackState -> {
                saveMediaState()
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
            is PlayerEvent.SetIsPlayingState -> _playerState.update {
                it.copy(isPlaying = event.value)
            }
            is PlayerEvent.SetPlayWhenReady -> _playerState.update {
                it.copy(playWhenReady = event.value)
            }
            is PlayerEvent.PlayerError -> _playerState.update {
                it.copy(error = event.value)
            }
            is PlayerEvent.AddAudioTracks -> _playerState.update {
                it.copy(audioTracks = event.value)
            }
            is PlayerEvent.MediaItemTransition -> {
                saveMediaState()
                _playerState.update { state ->
                    state.copy(
                        currentPlayingMedia = playerState.value.mediaList.first { it.id == event.value }
                    )
                }
                restoreMediaState()
            }
            is PlayerEvent.SwitchAudioTrack -> {
                val audioGroup = playerHelper
                    .getTrackGroupFromFormatId(C.TRACK_TYPE_AUDIO, event.value)
                audioGroup?.let {
                    if (!it.isSelected && it.isSupported) {
                        playerHelper.exoPlayer.trackSelectionParameters = playerHelper.exoPlayer
                            .trackSelectionParameters
                            .buildUpon()
                            .setOverrideForType(
                                TrackSelectionOverride(it.mediaTrackGroup, 0)
                            ).build()
                    }
                }
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
            is PlayerEvent.PlaybackState -> _playerState.update {
                it.copy(playbackState = event.value)
            }
            is PlayerEvent.PlaybackStarted -> _playerState.update {
                it.copy(playbackStarted = event.value)
            }
        }
    }
}

data class PlayerState(
    val volume: Int = 0,
    val maxLevel: Int = 25,
    val brightness: Int = 5,
    val playbackState: Int = 0,
    val error: Boolean = false,
    val isPlaying: Boolean = true,
    val playWhenReady: Boolean = true,
    val playbackStarted: Boolean = false,
    val currentMediaItemDuration: Long = 0,
    val mediaList: List<Media> = emptyList(),
    val currentPlayingMedia: Media = Media(),
    val audioTracks: List<AudioTrack> = emptyList(),
    val screenOrientation: Orientation = Orientation.PORTRAIT
)


data class PlayerUiState(
    val isSeekBarVisible: Boolean = false,
    val isVolumeBarVisible: Boolean = false,
    val isControllerVisible: Boolean = false,
    val isBrightnessBarVisible: Boolean = false,
    val isAudioTrackDialogVisible: Boolean = false,
)

sealed interface UiEvent {
    object ToggleShowUi : UiEvent
    object SavePlaybackState : UiEvent
    object ToggleAspectRatio : UiEvent
    data class ShowAudioTrackDialog(val value: Boolean) : UiEvent
    data class ShowUi(val value: Boolean) : UiEvent
    data class ShowSeekBar(val value: Boolean) : UiEvent
    data class ShowVolumeBar(val value: Boolean) : UiEvent
    data class ShowBrightnessBar(val value: Boolean) : UiEvent
    data class SeekToMediaItem(val value: Int) : UiEvent
}

sealed interface PlayerEvent {
    object IncreaseVolume : PlayerEvent
    object DecreaseVolume : PlayerEvent
    object IncreaseBrightness : PlayerEvent
    object DecreaseBrightness : PlayerEvent
    data class SetVolume(val value: Int) : PlayerEvent
    data class SetDuration(val value: Long) : PlayerEvent
    data class SetIsPlayingState(val value: Boolean) : PlayerEvent
    data class SetPlayWhenReady(val value: Boolean) : PlayerEvent
    data class SetOrientation(val value: Orientation) : PlayerEvent
    data class MediaItemTransition(val value: Long) : PlayerEvent
    data class AddAudioTracks(val value: List<AudioTrack>) : PlayerEvent
    data class SwitchAudioTrack(val value: String) : PlayerEvent
    data class PlayerError(val value: Boolean) : PlayerEvent
    data class PlaybackState(val value: Int): PlayerEvent
    data class PlaybackStarted(val value: Boolean): PlayerEvent
}

data class AudioTrack(
    val displayName: String,
    val formatId: String,
    val isSelected: Boolean
)