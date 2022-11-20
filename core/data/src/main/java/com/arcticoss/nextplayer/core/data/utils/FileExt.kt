package com.arcticoss.nextplayer.core.data.utils

import android.webkit.MimeTypeMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.net.URLConnection
import java.util.*

private const val TAG = "FileExt"

fun File.notExists(): Boolean = !this.exists()

fun File.isVideoFile(): Boolean {
    val path = this.path
    var mimeType = URLConnection.guessContentTypeFromName(path)
    if (mimeType == null) {
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)
    }
    return mimeType != null && mimeType.startsWith("video")
}


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


fun File.getFoldersAndVideos() = flow {
    val folderStack: Stack<File> = Stack()
    folderStack.add(this@getFoldersAndVideos)
    emit(this@getFoldersAndVideos)
    while (folderStack.isNotEmpty()) {
        for (file in folderStack.pop().listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                folderStack.add(file)
                emit(file)
            } else if (file.isVideoFile()) {
                emit(file)
            }
        }
    }
}