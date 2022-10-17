package com.arcticoss.data.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.net.URLConnection
import java.util.*

fun File.isVideoFile(): Boolean {
    val path = this.path.lowercase()
    val mimeType = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("video")
}

fun File.getVideos(): Flow<List<File>> = flow {
    val videoFiles = mutableListOf<File>()
    val folderStack: Stack<File> = Stack()
    folderStack.add(this@getVideos)
    while (folderStack.isNotEmpty()) {
        for (file in folderStack.pop().listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                folderStack.add(file)
            } else if (file.isVideoFile()) {
                videoFiles.add(file)
                emit(videoFiles.toList())
            }
        }
    }
}