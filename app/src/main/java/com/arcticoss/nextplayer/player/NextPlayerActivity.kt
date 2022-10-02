package com.arcticoss.nextplayer.player

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.arcticoss.nextplayer.player.ui.playerscreen.NextPlayerScreen
import com.arcticoss.nextplayer.player.ui.theme.NextPlayerTheme
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "NextPlayerActivity"

@AndroidEntryPoint
class NextPlayerActivity : ComponentActivity() {
    @Inject lateinit var player: Player
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
                    NextPlayerScreen(
                        mediaPath = videoFilePath,
                        player = player,
                        onVisibilityChange = { visibility ->
                            when (visibility) {
                                true -> showSystemBars()
                                false -> hideSystemBars()
                            }
                        },
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}


