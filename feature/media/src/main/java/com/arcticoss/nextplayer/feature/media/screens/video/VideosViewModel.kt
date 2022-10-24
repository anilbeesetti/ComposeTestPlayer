package com.arcticoss.nextplayer.feature.media.screens.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.model.InterfacePreferences
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetSortedMediaFolderStreamUseCase
import com.arcticoss.nextplayer.feature.media.navigation.folderIdArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interfacePreferencesDataSource: InterfacePreferencesDataSource,
    private val getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase
) : ViewModel() {

    private val folderId = savedStateHandle.get<Long>(folderIdArg)

    private val _videosUiState = MutableStateFlow(VideoScreenUiState())
    val videosUiState = _videosUiState.asStateFlow()

    val interfacePreferences = interfacePreferencesDataSource
        .preferencesFlow
        .onEach {
            folderId?.let { id ->
                getMedia(id, it.showHidden, it.sortBy, it.sortOrder)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InterfacePreferences()
        )


    private fun getMedia(id: Long, showHidden: Boolean, sortBy: SortBy, sortOrder: SortOrder) {
        getSortedMediaFolderStream.getMedia(id, showHidden, sortBy, sortOrder).onEach {
            _videosUiState.value = _videosUiState.value.copy(
                mediaFolder = it
            )
        }.launchIn(viewModelScope)
    }
}

data class VideoScreenUiState(
    val mediaFolder: MediaFolder = MediaFolder(),
    val isLoading: Boolean = false,
)