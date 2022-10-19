package com.arcticoss.feature.media.presentation.composables

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.media.MediaScreenViewModel
import com.arcticoss.feature.player.PlayerActivity

private const val TAG = "ShowVideoFiles"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ShowVideoFiles(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: MediaScreenViewModel = hiltViewModel()
) {
    val media by viewModel.mediaItemList.collectAsStateWithLifecycle()
    val mediaUiState by viewModel.mediaUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (mediaUiState.isLoading && media.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (media.isEmpty()) {
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
            items(media, key = { it.id }) { mediaItem ->
                MediaListItem(
                    mediaItem = mediaItem,
                    onClick = { startPlayerActivity(context, mediaItem.path) }
                )
            }
        }
    }

}

fun startPlayerActivity(context: Context, path: String) {
    val intent = Intent(context, PlayerActivity::class.java).also {
        it.putExtra("videoFilePath", path)
    }
    context.startActivity(intent)
}