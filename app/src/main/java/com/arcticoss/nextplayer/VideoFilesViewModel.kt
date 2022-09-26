package com.arcticoss.nextplayer

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class VideoFilesViewModel: ViewModel() {

    private val _videoFiles = MutableStateFlow(listOf<File>())
    val videoFiles = _videoFiles.asStateFlow()

    init {
        viewModelScope.launch {
            _videoFiles.value = Environment.getExternalStorageDirectory().getVideos()
        }
    }
}