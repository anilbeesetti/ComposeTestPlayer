package com.arcticoss.nextplayer

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.io.File


@Composable
fun ShowVideoFiles(videoFiles: List<File>) {
    LazyColumn() {
        items(videoFiles) { video ->
            VideoFileItem(video)
        }
    }
}

@Composable
fun VideoFileItem(videoFile: File) {
    Text(videoFile.name)
}
