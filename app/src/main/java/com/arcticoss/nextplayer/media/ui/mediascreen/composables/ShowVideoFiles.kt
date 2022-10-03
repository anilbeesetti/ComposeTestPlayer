package com.arcticoss.nextplayer.media.ui.mediascreen.composables

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.player.NextPlayerActivity
import java.io.File


@Composable
fun ShowVideoFiles(
    videoFiles: List<File>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (videoFiles.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No videos found.", style = MaterialTheme.typography.labelLarge)
        }
    } else {
        LazyColumn(
            contentPadding = contentPadding,
            modifier = modifier.fillMaxWidth()
        ) {
            item {
                Spacer(modifier = Modifier.height(5.dp))
            }
            items(videoFiles) { video ->
                VideoFileItem(
                    videoFile = video,
                    onClick = { startPlayerActivity(context, video.path) }
                )
            }
        }
    }
}

fun startPlayerActivity(context: Context, path: String) {
    val intent = Intent(context, NextPlayerActivity::class.java).also {
        it.putExtra("videoFilePath", path)
    }
    context.startActivity(intent)
}