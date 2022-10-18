package com.arcticoss.nextplayer.media.ui.mediascreen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arcticoss.model.MediaItem
import java.io.File

@Composable
fun MediaItem(
    mediaItem: MediaItem,
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
                .widthIn(max = 500.dp)
                .fillMaxWidth(0.40f)
                .aspectRatio((1.5).toFloat())
                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = mediaItem.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

}