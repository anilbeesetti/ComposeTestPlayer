package com.arcticoss.nextplayer.feature.media.screens.video

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.media.R
import com.arcticoss.nextplayer.feature.media.composables.CheckPermissionAndSetContent
import com.arcticoss.nextplayer.feature.media.composables.ShowVideoFiles

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun VideosScreen(
    onNavItemClick: () -> Unit = {},
    onMediaItemClick: (path: String) -> Unit,
    viewModel: VideosViewModel = hiltViewModel(),
) {
    val uiState by viewModel.videosUiState.collectAsStateWithLifecycle()

    CheckPermissionAndSetContent(
        title = {
            Text(
                text = uiState.mediaFolder.name
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavItemClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_arrow)
                )
            }
        }
    ) { innerPadding ->
        ShowVideoFiles(
            isLoading = uiState.isLoading,
            mediaItems = uiState.mediaFolder.mediaItems,
            contentPadding = innerPadding,
            onMediaItemClick = onMediaItemClick,
        )
    }
}