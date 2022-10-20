package com.arcticoss.feature.media

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.feature.media.domain.MediaFolderStreamUseCase
import com.arcticoss.feature.media.domain.MediaItemStreamUseCase
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.MediaItem
import com.arcticoss.model.MediaPreferences
import com.arcticoss.nextplayer.core.datastore.datasource.MediaPreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "VideoFilesViewModel"

@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    private val mediaRepository: IMediaRepository,
    private val mediaItemStreamUseCase: MediaItemStreamUseCase,
    private val mediaFolderStreamUseCase: MediaFolderStreamUseCase,
    private val mediaPreferencesDataSource: MediaPreferencesDataSource
) : ViewModel() {

    private val _mediaUiState = MutableStateFlow(MediaUiState())
    val mediaUiState = _mediaUiState.asStateFlow()

    private val mediaPreferenceFlow = mediaPreferencesDataSource.mediaPrefStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MediaPreferences()
        )

    val mediaItemList: StateFlow<List<MediaItem>> = mediaItemStreamUseCase(
        mediaPreferenceFlow.value.showHidden,
        mediaPreferenceFlow.value.sortBy,
        mediaPreferenceFlow.value.sortOrder
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val mediaFolderList: StateFlow<List<MediaFolder>> = mediaFolderStreamUseCase(
        mediaPreferenceFlow.value.showHidden,
        mediaPreferenceFlow.value.sortBy,
        mediaPreferenceFlow.value.sortOrder
    ).stateIn(
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