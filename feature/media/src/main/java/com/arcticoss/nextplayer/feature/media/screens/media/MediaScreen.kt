package com.arcticoss.nextplayer.feature.media.screens.media

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.arcticoss.nextplayer.feature.media.R
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.media.composables.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.media.composables.CheckPermissionAndSetContent
import com.arcticoss.nextplayer.feature.media.composables.ShowVideoFiles
import com.arcticoss.nextplayer.feature.media.composables.ShowFolderItems

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    onNavigate: (NavigateTo) -> Unit,
    viewModel: MediaScreenViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val interfacePreferences by viewModel.interfacePreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.mediaUIState.collectAsStateWithLifecycle()

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            viewModel.syncMedia()
        }
    }

    CheckPermissionAndSetContent(
        title = {
            Text(
                text = stringResource(id = if (interfacePreferences.groupVideos) R.string.folders else R.string.videos)
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigate(NavigateTo.Settings) }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        }
    ) { innerPadding ->
        when(interfacePreferences.groupVideos) {
            true -> ShowFolderItems(
                isLoading = uiState.isLoading,
                mediaFolderList = uiState.mediaFolderList,
                contentPadding = innerPadding,
                onFolderItemClick = { onNavigate(NavigateTo.Videos(it)) }
            )
            false -> ShowVideoFiles(
                isLoading = uiState.isLoading,
                mediaItems = uiState.mediaItemList,
                contentPadding = innerPadding,
                onMediaItemClick = { onNavigate(NavigateTo.Player(it)) }
            )
        }
    }
}

sealed interface NavigateTo{
    object Settings: NavigateTo
    data class Player(val path: String): NavigateTo
    data class Videos(val folderId: Long): NavigateTo
}