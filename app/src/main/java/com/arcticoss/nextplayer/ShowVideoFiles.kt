package com.arcticoss.nextplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.File


@Composable
fun ShowVideoFiles(videoFiles: List<File>) {
    LazyColumn {
        item { 
            Spacer(modifier = Modifier.height(10.dp))
        }
        items(videoFiles) { video ->
            VideoFileItem(
                videoFile = video,
                modifier = Modifier.padding(top = 0.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
            )
        }
    }
}

@Composable
fun VideoFileItem(
    videoFile: File,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
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
