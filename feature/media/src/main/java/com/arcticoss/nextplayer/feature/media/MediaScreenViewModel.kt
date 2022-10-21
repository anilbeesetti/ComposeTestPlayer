package com.arcticoss.nextplayer.feature.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.*
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.MediaFolderStreamUseCase
import com.arcticoss.nextplayer.core.domain.MediaItemStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "VideoFilesViewModel"

@HiltViewModel
class MediaScreenViewModel @Inject constructor(
    private val mediaRepository: IMediaRepository,
    private val mediaItemStreamUseCase: MediaItemStreamUseCase,
    private val mediaFolderStreamUseCase: MediaFolderStreamUseCase,
    private val interfacePreferencesDataSource: InterfacePreferencesDataSource
) : ViewModel() {

    private var syncMediaJob: Job? = null

    private val _mediaUiState = MutableStateFlow(MediaUiState())
    val mediaUiState = _mediaUiState.asStateFlow()


    private val _interfacePreferencesFlow = MutableStateFlow(InterfacePreferences())
    val interfacePreferencesFlow = _interfacePreferencesFlow.asStateFlow()

//    TODO: this way flow is not working
//    private val interfacePreferences = interfacePreferencesDataSource
//        .interfacePreferencesStream
//        .onEach {
//            getMediaItemFlow()
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = InterfacePreferences()
//        )

    init {
        getInterfacePreferencesFlow()
    }

    private fun getInterfacePreferencesFlow() {
        interfacePreferencesDataSource.interfacePreferencesStream.onEach {
            getMediaItemFlow(it.showHidden, it.sortBy, it.sortOrder)
        }.launchIn(viewModelScope)
    }

    private fun getMediaItemFlow(showHidden:Boolean, sortBy: SortBy, sortOrder: SortOrder) {
        mediaItemStreamUseCase(showHidden, sortBy, sortOrder).onEach {
            _mediaUiState.value = _mediaUiState.value.copy(
                isLoading = false,
                mediaItemList = it
            )
        }.launchIn(viewModelScope)
    }

    private fun getMediaFolderFlow(showHidden:Boolean, sortBy: SortBy, sortOrder: SortOrder) {
        mediaFolderStreamUseCase(showHidden, sortBy, sortOrder).onEach {
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
}

data class MediaUiState(
    val isLoading: Boolean = true,
    val mediaItemList: List<MediaItem> = emptyList(),
    val mediaFolderList: List<MediaFolder> = emptyList()
)