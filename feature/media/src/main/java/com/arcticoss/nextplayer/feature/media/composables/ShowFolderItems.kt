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
import androidx.compose.ui.unit.dp
import com.arcticoss.model.MediaFolder

@Composable
fun ShowFolderItems(
    isLoading: Boolean,
    mediaFolderList: List<MediaFolder>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onFolderItemClick: (Long) -> Unit
) {
    if (isLoading && mediaFolderList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (mediaFolderList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No folders with videos found.",
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        LazyColumn(
            contentPadding = contentPadding,
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(5.dp))
            }
            items(mediaFolderList, key = { it.id }) { mediaFolder ->
                FolderItem(
                    mediaFolder = mediaFolder,
                    onClick = { onFolderItemClick(mediaFolder.id) }
                )
            }
        }
    }
}