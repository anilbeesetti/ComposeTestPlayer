package com.arcticoss.nextplayer.feature.media.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.core.domain.models.Folder

@Composable
fun ShowFolderItems(
    isLoading: Boolean,
    folderList: List<Folder>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onFolderItemClick: (Long) -> Unit
) {
    if (isLoading && folderList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (folderList.isEmpty()) {
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
            items(folderList, key = { it.id }) { folder ->
                FolderItem(
                    folder = folder,
                    onClick = { onFolderItemClick(folder.id) }
                )
            }
        }
    }
}