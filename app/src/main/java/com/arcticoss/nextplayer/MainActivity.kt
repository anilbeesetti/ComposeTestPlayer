package com.arcticoss.nextplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arcticoss.nextplayer.media.ui.mediascreen.MediaScreen
import com.arcticoss.nextplayer.media.ui.theme.NextPlayerTheme
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"


@AndroidEntryPoint
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
                    MediaScreen()
                }
            }
        }
    }
}
