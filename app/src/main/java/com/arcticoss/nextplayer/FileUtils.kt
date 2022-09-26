package com.arcticoss.nextplayer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLConnection
import java.util.*

fun File.isVideoFile(): Boolean {
    val path = this.path.lowercase()
    val mimeType = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("video")
}

suspend fun File.getVideosR(): List<File> = withContext(Dispatchers.IO) {
    val videoFiles: MutableList<File> = mutableListOf()
    val files = this@getVideosR.listFiles() ?: emptyArray()
    for (file in files) {
        if (file.isVideoFile()) {
            videoFiles.add(file)
        } else if (file.isDirectory) {
            videoFiles.addAll(file.getVideosR())
        }
    }
    videoFiles.toList()
}

suspend fun File.getVideos(): List<File> = withContext(Dispatchers.IO) {
    val videoFiles = mutableListOf<File>()
    val folderStack: Stack<File> = Stack()
    folderStack.add(this@getVideos)
    while (folderStack.isNotEmpty()) {
        for (file in folderStack.pop().listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                folderStack.add(file)
            } else if (file.isVideoFile()) {
                videoFiles.add(file)
            }
        }
    }
    videoFiles.toList()
}