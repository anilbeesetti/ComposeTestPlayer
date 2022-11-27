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
import com.arcticoss.nextplayer.core.model.ResizeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    fun invokeMedia(uri: Uri) {
        viewModelScope.launch {
            val mediaItem = getMediaFromUri(uri)
            mediaItem?.let { media ->
                _playerViewState.update { it.copy(mediaList = listOf(media)) }
            }
        }
    }


    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.SaveState -> saveState(event.state)
            is UIEvent.ShowDialog -> showDialog(event.dialog)
            is UIEvent.SwitchResizeMode -> switchAspectRatio(event.resizeMode)
        }
    }

    private fun saveState(state: PersistableState) {
        viewModelScope.launch {
            val media = playerViewState.value.mediaList[state.index]
            _playerViewState.update {
                it.copy(
                    currentMediaItemId = media.id,
                    playWhenReady = state.playWhenReady
                )
            }
            mediaRepository.updateMedia(
                media.id,
                state.position,
                state.audioTrackId,
                state.subtitleTrackId
            )
            state.brightness?.let {
                preferencesDataSource.updateBrightnessLevel(it)
            }
        }
    }

    private fun showDialog(dialog: Dialog) {
        _playerViewState.update { it.copy(showDialog = dialog) }
    }

    private fun switchAspectRatio(resizeMode: ResizeMode?) {
        if (resizeMode == null) {
            viewModelScope.launch {
                preferencesDataSource.switchAspectRatio()
            }
        } else {
            // TODO
        }
    }

}

data class PlayerViewState(
    val playWhenReady: Boolean = true,
    val currentMediaItemId: Long? = null,
    val mediaList: List<Media> = emptyList(),
    val showDialog: Dialog = Dialog.None
)


sealed class UIEvent {

    /**
     * Show dialog
     */
    data class ShowDialog(val dialog: Dialog) : UIEvent()

    /**
     * Save State
     */
    data class SaveState(val state: PersistableState) : UIEvent()

    /**
     * @param resizeMode if it is null toggle between [ResizeMode]
     * if specified switches to the given [ResizeMode]
     */
    data class SwitchResizeMode(val resizeMode: ResizeMode? = null) : UIEvent()
}


data class PersistableState(
    val index: Int,
    val position: Long,
    val playWhenReady: Boolean,
    val brightness: Int? = null,
    val audioTrackId: String? = null,
    val subtitleTrackId: String? = null
)

enum class Dialog {
    AudioTrack,
    SubtitleTrack,
    None
}