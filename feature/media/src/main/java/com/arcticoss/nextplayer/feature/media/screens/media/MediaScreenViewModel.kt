package com.arcticoss.nextplayer.feature.media.screens.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.*
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetSortedMediaFoldersStreamUseCase
import com.arcticoss.nextplayer.core.domain.GetSortedMediaItemsStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    interfacePreferencesDataSource: InterfacePreferencesDataSource,
    private val mediaRepository: IMediaRepository,
    private val getSortedMediaFoldersStream: GetSortedMediaFoldersStreamUseCase,
    private val getSortedMediaItemsStream: GetSortedMediaItemsStreamUseCase
) : ViewModel() {

    private val _mediaUIState = MutableStateFlow(MediaUIState())
    val mediaUIState = _mediaUIState.asStateFlow()

    private var syncMediaJob: Job? = null

    val interfacePreferences = interfacePreferencesDataSource
        .preferencesFlow
        .onEach {
            when(it.groupVideos) {
                true -> getAllFolders(it.showHidden, it.sortBy, it.sortOrder)
                false -> getAllMedia(it.showHidden, it.sortBy, it.sortOrder)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InterfacePreferences()
        )


    fun syncMedia() {
        _mediaUIState.value = _mediaUIState.value.copy(isLoading = true)
        if (syncMediaJob == null) {
            syncMediaJob = viewModelScope.launch {
                mediaRepository.syncMedia()
                _mediaUIState.value = _mediaUIState.value.copy(isLoading = false)
            }
        } else if (!syncMediaJob!!.isActive) {
            syncMediaJob = viewModelScope.launch {
                mediaRepository.syncMedia()
                _mediaUIState.value = _mediaUIState.value.copy(isLoading = false)
            }
        }
    }

    private fun getAllFolders(showHidden: Boolean, sortBy: SortBy, sortOrder: SortOrder) {
        getSortedMediaFoldersStream.getAllMedia(showHidden, sortBy, sortOrder).onEach {
            _mediaUIState.value = _mediaUIState.value.copy(
                mediaFolderList = it
            )
        }.launchIn(viewModelScope)
    }

    private fun getAllMedia(showHidden: Boolean, sortBy: SortBy, sortOrder: SortOrder) {
        getSortedMediaItemsStream.getAllMedia(showHidden, sortBy, sortOrder).onEach {
            _mediaUIState.value = _mediaUIState.value.copy(
                mediaItemList = it
            )
        }.launchIn(viewModelScope)
    }
}


data class MediaUIState(
    val isLoading: Boolean = true,
    val mediaFolderList: List<MediaFolder> = emptyList(),
    val mediaItemList: List<MediaItem> = emptyList()
)