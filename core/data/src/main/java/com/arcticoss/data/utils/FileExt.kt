package com.arcticoss.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLConnection
import java.util.*

fun File.isVideoFile(): Boolean {
    val path = this.path.lowercase()
    val mimeType = URLConnection.guessContentTypeFromName(path)
    return mimeType != null && mimeType.startsWith("video")
}

//fun File.getVideos(): Flow<List<File>> = flow {
//    val videoFiles = mutableListOf<File>()
//    val folderStack: Stack<File> = Stack()
//    folderStack.add(this@getVideos)
//    while (folderStack.isNotEmpty()) {
//        for (file in folderStack.pop().listFiles() ?: emptyArray()) {
//            if (file.isDirectory) {
//                folderStack.add(file)
//            } else if (file.isVideoFile()) {
//                videoFiles.add(file)
//                emit(videoFiles.toList())
//            }
//        }
//    }
//}


fun File.getVideos(): Flow<File> = flow {
    val folderStack: Stack<File> = Stack()
    folderStack.add(this@getVideos)
    while (folderStack.isNotEmpty()) {
        for (file in folderStack.pop().listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                folderStack.add(file)
            } else if (file.isVideoFile()) {
                emit(file)
            }
        }
    }
}


fun File.getFolders(): List<File> {
    val folders = mutableListOf<File>()
    val folderStack: Stack<File> = Stack()
    folderStack.add(this@getFolders)
    folders.add(this@getFolders)
    while (folderStack.isNotEmpty()) {
        for (file in folderStack.pop().listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                folderStack.add(file)
                folders.add(file)
            }
        }
    }
    return folders.toList()
}