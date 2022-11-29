package com.arcticoss.nextplayer.feature.media.screens.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.data.repository.UiPreferencesRepository
import com.arcticoss.nextplayer.core.domain.GetSortedMediaFolderStreamUseCase
import com.arcticoss.nextplayer.core.model.Folder
import com.arcticoss.nextplayer.core.model.UiPreferences
import com.arcticoss.nextplayer.feature.media.navigation.folderIdArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    preferencesRepository: UiPreferencesRepository,
    getSortedMediaFolderStream: GetSortedMediaFolderStreamUseCase
) : ViewModel() {

    private val folderId = savedStateHandle.get<Long>(folderIdArg)

    val videoUiState: StateFlow<VideoUiState> = if (folderId == null) {
        MutableStateFlow(VideoUiState.Error("Not Found"))
    } else {
        getSortedMediaFolderStream(folderId)
            .map { VideoUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = VideoUiState.Loading
            )
    }

    val preferences = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiPreferences()
        )
}

sealed interface VideoUiState {
    object Loading : VideoUiState
    data class Success(val folder: Folder) : VideoUiState
    data class Error(val error: String) : VideoUiState
}