package com.arcticoss.nextplayer.feature.media.screens.media

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.media.R
import com.arcticoss.nextplayer.feature.media.composables.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.media.composables.CheckPermissionAndSetContent
import com.arcticoss.nextplayer.feature.media.composables.FolderItem
import com.arcticoss.nextplayer.feature.media.composables.MediaListItem

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    onNavigate: (NavigateTo) -> Unit,
    viewModel: MediaScreenViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val interfacePreferences by viewModel.interfacePreferences.collectAsStateWithLifecycle()
    val uiState by viewModel.mediaUIState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            viewModel.syncMedia()
        }
    }

    CheckPermissionAndSetContent(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = if (interfacePreferences.groupVideos) R.string.folders else R.string.videos))
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { onNavigate(NavigateTo.Settings) }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        /**
         * NOTE:-
         * 1. Passing innerPadding [PaddingValues] to other user defined composables
         *  containing [LazyColumn] causes to many recompositions
         * 2. This leads to a laggy [LazyColumn] experience
         */
        when (interfacePreferences.groupVideos) {
            true ->
                if (uiState.isLoading && uiState.mediaFolderList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.mediaFolderList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
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
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        items(uiState.mediaFolderList, key = { it.id }) { mediaFolder ->
                            FolderItem(
                                mediaFolder = mediaFolder,
                                onClick = { onNavigate(NavigateTo.Videos(mediaFolder.id)) }
                            )
                        }
                    }
                }
            false ->
                if (uiState.isLoading && uiState.mediaItemList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.mediaItemList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No videos found.", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    LazyColumn(
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                        items(uiState.mediaItemList, key = { it.id }) { mediaItem ->
                            MediaListItem(
                                mediaItem = mediaItem,
                                onClick = { onNavigate(NavigateTo.Player(mediaItem.path)) }
                            )
                        }
                    }
                }
        }
    }
}

sealed interface NavigateTo {
    object Settings : NavigateTo
    data class Player(val path: String) : NavigateTo
    data class Videos(val folderId: Long) : NavigateTo
}