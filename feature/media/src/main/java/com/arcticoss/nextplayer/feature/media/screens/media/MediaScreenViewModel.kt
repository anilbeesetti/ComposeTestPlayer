package com.arcticoss.nextplayer.feature.media.screens.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.data.repository.IMediaRepository
import com.arcticoss.nextplayer.core.model.InterfacePreferences
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetSortedFoldersStreamUseCase
import com.arcticoss.nextplayer.core.domain.GetSortedMediaItemsStreamUseCase
import com.arcticoss.nextplayer.core.domain.models.Folder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    preferencesDataSource: InterfacePreferencesDataSource,
    private val mediaRepository: IMediaRepository,
    getSortedFoldersStream: GetSortedFoldersStreamUseCase,
    getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase
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

    val mediaUiState: StateFlow<MediaUiState> =
        getSortedMediaItemsStream().map { MediaUiState.Success(it) }
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
    data class Success(val mediaItems: List<Media>) : MediaUiState
    object Loading : MediaUiState
}