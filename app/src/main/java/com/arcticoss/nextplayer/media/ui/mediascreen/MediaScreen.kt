package com.arcticoss.nextplayer.media.ui.mediascreen

import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.MediaLargeTopAppBar
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowContentForMarshMellow
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowContentForRedVelvet
import com.arcticoss.nextplayer.media.ui.mediascreen.composables.ShowVideoFiles
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.AddLifecycleEventObserver


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    viewModel: MediaScreenViewModel = hiltViewModel(),
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lifecycleOwner = LocalLifecycleOwner.current

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner,) { event ->
        if (event == Lifecycle.Event.ON_START) {
            viewModel.syncMedia()
        }
    }

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
            ShowContentForRedVelvet(contentPadding = innerPadding)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ShowContentForMarshMellow(contentPadding = innerPadding)
        } else {
            ShowVideoFiles(contentPadding = innerPadding)
        }
    }
}


