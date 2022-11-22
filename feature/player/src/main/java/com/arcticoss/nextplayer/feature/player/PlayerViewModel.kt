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
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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
    val player: Player,
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
        _playerViewState.update { it.copy(mediaList = mediaList) }
        val index = mediaList.indexOfFirst { it.id == mediaID }
        val mediaItems = playerViewState.value.mediaList.map {
            MediaItem.Builder().setUri(File(it.path).toUri()).setMediaId(it.id.toString())
                .build()
        }
        player.setMediaItems(mediaItems)
        player.prepare()
        player.seekTo(index, C.TIME_UNSET)
    }

    fun showDialog(dialog: Dialog) {
        _playerViewState.update { it.copy(showDialog = dialog) }
    }

    override fun onCleared() {
        player.release()
    }
}

data class PlayerViewState(
    val mediaList: List<Media> = emptyList(),
    val showDialog: Dialog = Dialog.None
)

enum class Dialog {
    AudioTrack,
    SubtitleTrack,
    None
}