package com.arcticoss.nextplayer.feature.player

import android.net.Uri
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaRepository: IMediaRepository,
    private val preferencesDataSource: PlayerPreferencesDataSource,
    private val getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase,
    private val getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase,
    private val getMediaFromUri: GetMediaFromUriUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaID = savedStateHandle.get<Long>("mediaID")
    private val folderID = savedStateHandle.get<Long>("folderID")

    private val _playerViewState = MutableStateFlow(PlayerViewState())
    val playerViewState = _playerViewState.asStateFlow()


    val preferencesFlow = preferencesDataSource.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerPreferences()
        )

    init {
        folderID?.let { id ->
            if (id == 0L) {
                getSortedMediaItemsStream().onEach { mediaList ->
                    _playerViewState.update { it.copy(mediaList = mediaList) }
                }.launchIn(viewModelScope)
            } else {
                getSortedMediaFolderStream(id).onEach { mediaFolder ->
                    _playerViewState.update { it.copy(mediaList = mediaFolder.mediaList) }
                }.launchIn(viewModelScope)
            }
        }
        mediaID?.let { id ->
            _playerViewState.update { it.copy(currentMediaItemId = id) }
        }
    }

    fun saveState(index: Int, position: Long, playWhenReady: Boolean, brightness: Int? = null) {
        viewModelScope.launch {
            val media = playerViewState.value.mediaList[index]
            _playerViewState.update { it.copy(currentMediaItemId = media.id, playWhenReady = playWhenReady) }
            mediaRepository.updateMedia(media.id, position)
            brightness?.let {
                preferencesDataSource.updateBrightnessLevel(brightness)
            }
        }
    }


    fun invokeMedia(uri: Uri) {
        viewModelScope.launch {
            val mediaItem = getMediaFromUri(uri)
            mediaItem?.let { media ->
                _playerViewState.update { it.copy(mediaList = listOf(media)) }
            }
        }
    }

    fun showDialog(dialog: Dialog) {
        _playerViewState.update { it.copy(showDialog = dialog) }
    }

    fun switchAspectRatio() {
        viewModelScope.launch {
            preferencesDataSource.switchAspectRatio()
        }
    }
}

data class PlayerViewState(
    val playWhenReady: Boolean = true,
    val currentMediaItemId: Long? = null,
    val mediaList: List<Media> = emptyList(),
    val showDialog: Dialog = Dialog.None
)

enum class Dialog {
    AudioTrack,
    SubtitleTrack,
    None
}