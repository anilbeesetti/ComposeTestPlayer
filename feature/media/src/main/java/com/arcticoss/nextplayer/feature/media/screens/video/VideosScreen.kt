package com.arcticoss.nextplayer.feature.media.screens.video

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.media.R
import com.arcticoss.nextplayer.feature.media.composables.CheckPermissionAndSetContent
import com.arcticoss.nextplayer.feature.media.composables.MediaListItem

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideosScreen(
    onNavItemClick: () -> Unit = {},
    onMediaItemClick: (mediaId: Long, folderId: Long) -> Unit,
    viewModel: VideosViewModel = hiltViewModel(),
) {
    val mediaFolderState by viewModel.videoUiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    CheckPermissionAndSetContent(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = if (mediaFolderState is VideoUiState.Success)
                            (mediaFolderState as VideoUiState.Success).mediaFolder.name
                        else ""
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onNavItemClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (mediaFolderState) {
            is VideoUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No videos found.", style = MaterialTheme.typography.labelLarge)
                }
            }
            VideoUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is VideoUiState.Success -> {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    val mediaFolder = (mediaFolderState as VideoUiState.Success).mediaFolder
                    items(mediaFolder.mediaItems, key = { it.id }) { mediaItem ->
                        Log.d("TAG", "ShowVideoFiles: ${mediaItem.id}")
                        MediaListItem(
                            mediaItem = mediaItem,
                            onClick = { onMediaItemClick(mediaItem.id, mediaFolder.id) }
                        )
                    }
                }
            }
        }
    }
}