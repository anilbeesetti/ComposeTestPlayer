package com.arcticoss.nextplayer.feature.media.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.model.Media

private const val TAG = "ShowVideoFiles"

@Composable
fun ShowVideoFiles(
    isLoading: Boolean,
    mediaItems: List<Media>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onMediaItemClick: (path: String) -> Unit = {},
) {

    val context = LocalContext.current

    if (isLoading && mediaItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (mediaItems.isEmpty()) {
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
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(5.dp))
            }
                items(mediaItems, key = { it.id }) { mediaItem ->
                    MediaListItem(
                        mediaItem = mediaItem,
                        onClick = { onMediaItemClick(mediaItem.path) }
                    )
                }
        }
    }
}