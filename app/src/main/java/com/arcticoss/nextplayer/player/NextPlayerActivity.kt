package com.arcticoss.nextplayer.player

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.arcticoss.nextplayer.player.ui.playerscreen.NextPlayerScreen
import com.arcticoss.nextplayer.player.ui.theme.NextPlayerTheme
import com.arcticoss.nextplayer.player.utils.hideSystemBars
import com.arcticoss.nextplayer.player.utils.showSystemBars
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "NextPlayerActivity"

@AndroidEntryPoint
class NextPlayerActivity : ComponentActivity() {
    @Inject
    lateinit var player: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        val videoFilePath = intent.getStringExtra("videoFilePath") ?: ""
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        setContent {
            NextPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalContentColor provides Color.White) {
                        NextPlayerScreen(
                            mediaPath = videoFilePath,
                            player = player,
                            onBackPressed = { finish() }
                        )
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "onConfigurationChanged: ${newConfig.orientation}")
        super.onConfigurationChanged(newConfig)
    }
}


