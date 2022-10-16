package com.arcticoss.nextplayer.player

import android.os.Build
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "NextPlayerActivity"

@AndroidEntryPoint
class NextPlayerActivity : ComponentActivity() {
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
                            onBackPressed = { finish() }
                        )
                    }
                }
            }
        }
    }
}


