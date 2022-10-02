package com.arcticoss.nextplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.media.ui.mediascreen.MediaScreen
import com.arcticoss.nextplayer.media.ui.mediascreen.VideoFilesViewModel
import com.arcticoss.nextplayer.media.ui.theme.NextPlayerTheme


private const val TAG = "MainActivity"

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
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val viewModel: VideoFilesViewModel = viewModel()
                    MediaScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
