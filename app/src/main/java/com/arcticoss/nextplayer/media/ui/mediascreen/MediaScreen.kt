package com.arcticoss.nextplayer.media.ui.mediascreen

import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.MediaLargeTopAppBar
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowContentForMarshMellow
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowContentForRedVelvet
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowVideoFiles


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLifecycleComposeApi::class
)
@Composable
fun MediaScreen(
    viewModel: VideoFilesViewModel = viewModel(),
) {
    val mediaListState by viewModel.mediaListState.collectAsStateWithLifecycle()
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            MediaLargeTopAppBar(
                title = "NextPlayer",
                scrollBehavior = scrollBehaviour
            )
        }
    ) { innerPadding ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ShowContentForRedVelvet(mediaListState = mediaListState, contentPadding = innerPadding)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ShowContentForMarshMellow(mediaListState = mediaListState, contentPadding = innerPadding)
        } else {
            ShowVideoFiles(mediaListState = mediaListState, contentPadding = innerPadding)
        }
    }
}


