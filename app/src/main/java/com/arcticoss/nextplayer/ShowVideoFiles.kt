package com.arcticoss.nextplayer

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.player.NextPlayerActivity
import java.io.File


@Composable
fun ShowVideoFiles(videoFiles: List<File>) {
    val context = LocalContext.current
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(5.dp))
        }
        items(videoFiles) { video ->
            VideoFileItem(
                videoFile = video,
                onClick = { startPlayerActivity(context, video.path) }
            )
//            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

fun startPlayerActivity(context: Context, path: String) {
    val intent = Intent(context, NextPlayerActivity::class.java).also {
        it.putExtra("videoFilePath", path)
    }
    context.startActivity(intent)
}

@Composable
fun VideoFileItem(
    videoFile: File,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.40f)
                .height(100.dp)
                .background(Color.Cyan, MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = videoFile.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

}
