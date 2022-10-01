package com.arcticoss.nextplayer.player

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.arcticoss.nextplayer.player.ui.playerscreen.NextPlayerScreen
import com.arcticoss.nextplayer.player.ui.theme.NextPlayerTheme
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay

private const val TAG = "NextPlayerActivity"

class NextPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val videoFilePath = intent.getStringExtra("videoFilePath") ?: ""
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val player = ExoPlayer.Builder(this).build()
        setContent {
            var showUI by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(key1 = showUI) {
                if (showUI) {
                    delay(5000)
                    showUI = false
                    hideSystemBars()
                } else {
                    hideSystemBars()
                }
            }
            NextPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NextPlayerScreen(
                        showUI = showUI,
                        mediaPath = videoFilePath,
                        exoPlayer = player,
                        onVisibilityChange = { visibility ->
                            when (visibility) {
                                true -> {
                                    showUI = true
                                    showSystemBars()
                                }
                                false -> {
                                    showUI = false
                                    hideSystemBars()
                                }
                            }
                        },
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}


