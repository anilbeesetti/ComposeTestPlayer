package com.arcticoss.nextplayer.media.ui.mediascreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "VideoFilesViewModel"

@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    private val mediaRepository: IMediaRepository
) : ViewModel() {

    private val _mediaUiState = MutableStateFlow(MediaUiState())
    val mediaUiState = _mediaUiState.asStateFlow()

    val mediaState: StateFlow<List<MediaItem>> = mediaRepository
        .getMediaStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private var syncMediaJob: Job? = null

    fun syncMedia() {
        Log.d(TAG, "syncMedia: syncing...")
        _mediaUiState.value = _mediaUiState.value.copy(isLoading = true)
        if (syncMediaJob == null) {
            syncMediaJob = viewModelScope.launch {
                mediaRepository.syncMedia()
                _mediaUiState.value = _mediaUiState.value.copy(isLoading = false)
            }
        } else if (!syncMediaJob!!.isActive) {
            syncMediaJob = viewModelScope.launch {
                mediaRepository.syncMedia()
                _mediaUiState.value = _mediaUiState.value.copy(isLoading = false)
            }
        }
    }
}

data class MediaUiState(
    val isLoading: Boolean = true
)