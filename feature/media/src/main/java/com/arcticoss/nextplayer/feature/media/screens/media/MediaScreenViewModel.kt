package com.arcticoss.nextplayer.feature.media.screens.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.*
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetSortedFoldersStreamUseCase
import com.arcticoss.nextplayer.core.domain.GetSortedMediaItemsStreamUseCase
import com.arcticoss.nextplayer.core.domain.models.Folder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    preferencesDataSource: InterfacePreferencesDataSource,
    private val mediaRepository: IMediaRepository,
    private val getSortedFoldersStream: GetSortedFoldersStreamUseCase,
    private val getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase
) : ViewModel() {

    private var syncMediaJob: Job? = null

    val preferencesStateFlow = preferencesDataSource
        .preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InterfacePreferences()
        )

    val folderUiState: StateFlow<FolderUiState> =
        getSortedFoldersStream().map { FolderUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FolderUiState.Loading
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val mediaUiState: StateFlow<MediaUiState> = preferencesDataSource
        .preferencesFlow.flatMapLatest { preferences ->
            getSortedMediaItemsStream
                .getAllMedia(preferences.showHidden, preferences.sortBy, preferences.sortOrder)
                .map { MediaUiState.Success(it) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MediaUiState.Loading
        )


    fun syncMedia() {
        if (syncMediaJob == null) {
            syncMediaJob = viewModelScope.launch {
                mediaRepository.syncMedia()
            }
        } else if (!syncMediaJob!!.isActive) {
            syncMediaJob = viewModelScope.launch {
                mediaRepository.syncMedia()
            }
        }
    }
}

sealed interface FolderUiState {
    data class Success(val folders: List<Folder>) : FolderUiState
    object Loading : FolderUiState
}

sealed interface MediaUiState {
    data class Success(val mediaItems: List<MediaItem>) : MediaUiState
    object Loading : MediaUiState
}