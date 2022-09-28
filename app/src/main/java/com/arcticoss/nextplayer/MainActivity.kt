package com.arcticoss.nextplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.ui.theme.NextPlayerTheme
import java.io.File
import java.nio.file.Files


private const val TAG = "MainActivity"

@OptIn(ExperimentalLifecycleComposeApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NextPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel<VideoFilesViewModel>()
                    val files by viewModel.videoFiles.collectAsStateWithLifecycle()
                    MediaScreen(videoFiles = files)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    videoFiles: List<File>
) {

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier,
        topBar = {
            MediaLargeTopAppBar(
                title = "NextPlayer",
                scrollBehavior = scrollBehaviour
            )
        }
    ) { innerPadding ->
        ShowVideoFiles(
            videoFiles = videoFiles,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

