package com.arcticoss.nextplayer.feature.media.screens.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.media.R
import com.arcticoss.nextplayer.feature.media.composables.CheckPermissionAndSetContent
import com.arcticoss.nextplayer.feature.media.composables.FolderItem
import com.arcticoss.nextplayer.feature.media.composables.MediaListItem

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    onNavigate: (NavigateTo) -> Unit,
    viewModel: MediaScreenViewModel = hiltViewModel(),
) {
    val interfacePreferences by viewModel.preferencesStateFlow.collectAsStateWithLifecycle()
    val folderUiState by viewModel.folderUiState.collectAsStateWithLifecycle()
    val mediaUiState by viewModel.mediaUiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    CheckPermissionAndSetContent(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.next_player))
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
         * 1. Passing innerPadding [PaddingValues] to other user defined composable
         *  containing [LazyColumn] causes to many recompositions
         * 2. This leads to a laggy [LazyColumn] experience
         */
        when (interfacePreferences.groupVideos) {
            true ->
                when(folderUiState) {
                    FolderUiState.Loading -> {
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
                    is FolderUiState.Success -> {
                        if ((folderUiState as FolderUiState.Success).folders.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "No folders with media found.", style = MaterialTheme.typography.labelLarge)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = innerPadding,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(5.dp))
                                }
                                items((folderUiState as FolderUiState.Success).folders, key = { it.id }) { mediaFolder ->
                                    FolderItem(
                                        folder = mediaFolder,
                                        onClick = { onNavigate(NavigateTo.Videos(mediaFolder.id)) }
                                    )
                                }
                            }
                        }
                    }
                }
            false ->
                when(mediaUiState) {
                    MediaUiState.Loading -> {
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
                    is MediaUiState.Success -> {
                        if ((mediaUiState as MediaUiState.Success).mediaItems.isEmpty()) {
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
                                items((mediaUiState as MediaUiState.Success).mediaItems, key = { it.id }) { mediaItem ->
                                    MediaListItem(
                                        mediaItem = mediaItem,
                                        onClick = { onNavigate(NavigateTo.Player(mediaItem.id)) }
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}

sealed interface NavigateTo {
    object Settings : NavigateTo
    data class Player(val mediaId: Long) : NavigateTo
    data class Videos(val folderId: Long) : NavigateTo
}