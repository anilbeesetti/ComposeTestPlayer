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
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaRepository: IMediaRepository,
    private val preferencesDataSource: PlayerPreferencesDataSource,
    private val getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase,
    private val getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase,
    private val getMediaFromUri: GetMediaFromUriUseCase,
    savedStateHandle: SavedStateHandle,
    normalPlayer: Player
) : ViewModel() {

    private val mediaID = savedStateHandle.get<Long>("mediaID")
    private val folderID = savedStateHandle.get<Long>("folderID")

    val player = normalPlayer as ExoPlayer

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState = _playerUiState.asStateFlow()

    val playerCurrentPosition = player
        .currentPositionAsFlow()
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

    fun invokeMedia(uri: Uri) {
        viewModelScope.launch {
            val mediaItem = getMediaFromUri(uri)
            mediaItem?.let { media ->
                setMedia(listOf(media))
            }
        }
    }

    init {
        folderID?.let { id ->
            if (id == 0L)
                getSortedMediaItemsStream().onEach { mediaList ->
                    setMedia(mediaList)
                }.launchIn(viewModelScope)
            else
                getSortedMediaFolderStream(id).onEach { mediaFolder ->
                    setMedia(mediaFolder.mediaList)
                }.launchIn(viewModelScope)
        }

    }

    private fun setMedia(mediaList: List<Media>) {
        _playerState.update { it.copy(mediaList = mediaList) }
        if (playerState.value.currentPlayingMedia.id == 0L) {
            val index = mediaList.indexOfFirst { it.id == mediaID }
            setPlayerMediaItems()
            moveToMediaItem(index)
        }
    }

    private fun setPlayerMediaItems() {
        val mediaItems = playerState.value.mediaList.map {
            MediaItem.Builder().setUri(File(it.path).toUri()).setMediaId(it.id.toString()).build()
        }
        player.setMediaItems(mediaItems)
        player.prepare()
    }

    private fun moveToMediaItem(index: Int) {
        if (index > player.currentMediaItemIndex) {
            for (i in player.currentMediaItemIndex until index) {
                player.seekToNextMediaItem()
            }
        } else {
            for (i in index until player.currentMediaItemIndex) {
                player.seekToPreviousMediaItem()
            }
        }
    }

    private fun savePlaybackState() {
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
            player.seekTo(playerState.value.currentPlayingMedia.lastPlayedPosition)
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
            UiEvent.SavePlaybackState -> {
                savePlaybackState()
            }
            is UiEvent.SeekToMediaItem -> {
                this.onUiEvent(UiEvent.SavePlaybackState)
                moveToMediaItem(event.value)
            }
            is UiEvent.ShowAudioTrackDialog -> _playerUiState.update {
                it.copy(isAudioTrackDialogVisible = event.value)
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
            is PlayerEvent.MediaItemTransition -> {
                savePlaybackState()
                _playerState.update { state ->
                    state.copy(
                        currentPlayingMedia = playerState.value.mediaList.first { it.id == event.value }
                    )
                }
                restoreMediaState()
            }
            is PlayerEvent.AddAudioTracks -> {
                _playerState.update { state ->
                    state.copy(
                        audioTracks = event.value
                    )
                }
            }
            is PlayerEvent.SwitchAudioTrack -> {
                val audioGroup = player.getTrackGroupFromFormatId(C.TRACK_TYPE_AUDIO, event.value)
                audioGroup?.let {
                    if (!it.isSelected && it.isSupported) {
                        player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                            .setOverrideForType(
                                TrackSelectionOverride(it.mediaTrackGroup, 0)
                            ).build()
                    }
                }
            }
        }
    }
}

data class PlayerState(
    val volume: Int = 0,
    val maxLevel: Int = 25,
    val brightness: Int = 5,
    val isPlaying: Boolean = true,
    val playWhenReady: Boolean = true,
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
    data class SetPlaybackState(val value: Boolean) : PlayerEvent
    data class SetPlayWhenReady(val value: Boolean) : PlayerEvent
    data class SetOrientation(val value: Orientation) : PlayerEvent
    data class MediaItemTransition(val value: Long) : PlayerEvent
    data class AddAudioTracks(val value: List<AudioTrack>) : PlayerEvent
    data class SwitchAudioTrack(val value: String) : PlayerEvent
}

fun ExoPlayer.currentPositionAsFlow() = flow {
    while (true) {
        emit(this@currentPositionAsFlow.currentPosition)
        delay(1.seconds / 30)
    }
}

fun ExoPlayer.getTrackGroupFromFormatId(trackType: Int, id: String):  Tracks.Group? {
    for (group in this.currentTracks.groups) {
        if (group.type == trackType) {
            val trackGroup = group.mediaTrackGroup
            val format: Format = trackGroup.getFormat(0)
            if (Objects.equals(id, format.id)) {
                return group
            }
        }
    }
    return null
}

data class AudioTrack(
    val displayName: String,
    val formatId: String,
    val isSelected: Boolean
)