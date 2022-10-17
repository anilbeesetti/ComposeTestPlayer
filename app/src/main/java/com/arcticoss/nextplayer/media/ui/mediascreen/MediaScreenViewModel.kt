package com.arcticoss.nextplayer.media.ui.mediascreen

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.utils.getVideos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "VideoFilesViewModel"

class MediaScreenViewModel : ViewModel() {

    private val _mediaListState = MutableStateFlow(MediaListState())
    val mediaListState = _mediaListState.asStateFlow()

    init {
        viewModelScope.launch {
            _mediaListState.value = mediaListState.value.copy(
                isLoading = true
            )
            val storage = Environment.getExternalStorageDirectory()
            val videos = storage.getVideos()
            _mediaListState.value = mediaListState.value.copy(
                isLoading = false,
                videoFiles = videos
            )
        }
    }
}

data class MediaListState(
    val isLoading: Boolean = true,
    val videoFiles: List<File> = emptyList()
)