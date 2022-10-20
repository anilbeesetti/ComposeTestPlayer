package com.arcticoss.feature.media

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.feature.media.domain.MediaFolderStreamUseCase
import com.arcticoss.feature.media.domain.MediaItemStreamUseCase
import com.arcticoss.model.*
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

    private var syncMediaJob: Job? = null

    private val _mediaUiState = MutableStateFlow(MediaUiState())
    val mediaUiState = _mediaUiState.asStateFlow()

    private val _mediaPreferencesFlow = MutableStateFlow(MediaPreferences())
    val mediaPreferencesFlow = _mediaPreferencesFlow.asStateFlow()

    init {
        getMediaPreferencesFlow()
    }

    private fun getMediaPreferencesFlow() {
        mediaPreferencesDataSource.mediaPrefStream.onEach {
            _mediaPreferencesFlow.value = it
            when(it.viewOption) {
                ViewOption.Videos -> getMediaItemFlow()
                ViewOption.Folders -> getMediaFolderFlow()
            }
        }.launchIn(viewModelScope)
    }

    private fun getMediaItemFlow() {
        mediaItemStreamUseCase(
            mediaPreferencesFlow.value.showHidden,
            mediaPreferencesFlow.value.sortBy,
            mediaPreferencesFlow.value.sortOrder
        ).onEach {
            _mediaUiState.value = _mediaUiState.value.copy(
                isLoading = false,
                mediaItemList = it
            )
        }.launchIn(viewModelScope)
    }

    private fun getMediaFolderFlow() {
        mediaFolderStreamUseCase(
            mediaPreferencesFlow.value.showHidden,
            mediaPreferencesFlow.value.sortBy,
            mediaPreferencesFlow.value.sortOrder
        ).onEach {
            _mediaUiState.value = _mediaUiState.value.copy(
                isLoading = false,
                mediaFolderList = it
            )
        }.launchIn(viewModelScope)
    }


    fun syncMedia() {
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

    fun toggleMediaView() {
        Log.d(TAG, "toggleMediaView: MediaScreen...toggle")
        viewModelScope.launch {
            mediaPreferencesDataSource.updateMediaPreferences(
                mediaPreferencesFlow.value.copy(
                    sortBy = if (mediaPreferencesFlow.value.sortBy == SortBy.Title) SortBy.Length else SortBy.Title
                )
            )
        }
    }
}

data class MediaUiState(
    val isLoading: Boolean = true,
    val mediaItemList: List<MediaItem> = emptyList(),
    val mediaFolderList: List<MediaFolder> = emptyList()
)