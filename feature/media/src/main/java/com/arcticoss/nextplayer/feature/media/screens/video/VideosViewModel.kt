package com.arcticoss.nextplayer.feature.media.screens.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.model.InterfacePreferences
import com.arcticoss.nextplayer.core.model.Folder
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.GetSortedMediaFolderStreamUseCase
import com.arcticoss.nextplayer.feature.media.navigation.folderIdArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    interfacePreferencesDataSource: InterfacePreferencesDataSource,
    getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase
) : ViewModel() {

    private val folderId = savedStateHandle.get<Long>(folderIdArg)

    val videoUiState: StateFlow<VideoUiState> = if (folderId == null) {
        MutableStateFlow(VideoUiState.Error(""))
    } else {
        getSortedMediaFolderStream(folderId)
            .map { VideoUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = VideoUiState.Loading
            )
    }

    val interfacePreferences = interfacePreferencesDataSource
        .preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InterfacePreferences()
        )
}

sealed class VideoUiState {
    object Loading: VideoUiState()
    data class Success(val folder: Folder): VideoUiState()
    data class Error(val error: String): VideoUiState()
}