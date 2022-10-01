package com.arcticoss.nextplayer.media.ui.mediascreen

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.utils.getVideos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "VideoFilesViewModel"

class VideoFilesViewModel : ViewModel() {

    private val _videoFiles = MutableStateFlow(listOf<File>())
    val videoFiles = _videoFiles.asStateFlow()

    init {
        viewModelScope.launch {
            val storage = Environment.getExternalStorageDirectory()
            val videos = storage.getVideos()
            _videoFiles.value = videos
        }
    }
}