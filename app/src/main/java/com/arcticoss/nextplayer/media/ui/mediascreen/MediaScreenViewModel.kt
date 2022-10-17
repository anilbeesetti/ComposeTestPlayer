package com.arcticoss.nextplayer.media.ui.mediascreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.data.repository.MediaItem
import com.arcticoss.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


private const val TAG = "VideoFilesViewModel"

@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _mediaListState = MutableStateFlow(MediaListState())
    val mediaListState = _mediaListState.asStateFlow()

    init {
        mediaRepository.getAllMedia().onEach {
            _mediaListState.value = _mediaListState.value.copy(
                isLoading = false,
                mediaItems = it
            )
        }.launchIn(viewModelScope)
    }
}

data class MediaListState(
    val isLoading: Boolean = true,
    val mediaItems: List<MediaItem> = emptyList()
)