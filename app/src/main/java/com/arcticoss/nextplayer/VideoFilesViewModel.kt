package com.arcticoss.nextplayer

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "VideoFilesViewModel"

class VideoFilesViewModel: ViewModel() {

    private val _videoFiles = MutableStateFlow(listOf<File>())
    val videoFiles = _videoFiles.asStateFlow()

    init {
        viewModelScope.launch {
            val storage = Environment.getExternalStorageDirectory()
            val videos = storage.getVideos()
            Log.d(TAG, "init: ${Environment.isExternalStorageManager()}")
            Log.d(TAG, "init: $storage")
            Log.d(TAG, "init: $videos")
            _videoFiles.value = videos
        }
    }
}