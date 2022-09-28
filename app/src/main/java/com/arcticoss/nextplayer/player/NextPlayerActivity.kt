package com.arcticoss.nextplayer.player

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import com.arcticoss.nextplayer.player.ui.theme.NextPlayerTheme
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File

class NextPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val videoFilePath = intent.getStringExtra("videoFilePath") ?: ""
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val player = ExoPlayer.Builder(this).build()
        setContent {
            NextPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen(
                        mediaPath = videoFilePath,
                        exoPlayer = player,
                        onVisibilityChange = { visibility ->
                            when(visibility) {
                                View.VISIBLE -> showSystemBars()
                                View.GONE -> hideSystemBars()
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun PlayerScreen(
    mediaPath: String,
    exoPlayer: ExoPlayer,
    onVisibilityChange: (visibility: Int) -> Unit
) {

    LaunchedEffect(exoPlayer) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(mediaPath)))
        exoPlayer.addMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    lateinit var playerView: StyledPlayerView
    DisposableEffect(
        AndroidView(
            factory = { context ->
                    playerView = StyledPlayerView(context).apply {
                    player = exoPlayer
                    setControllerVisibilityListener(
                        StyledPlayerView.ControllerVisibilityListener { onVisibilityChange(it) }
                    )
                    showController()
                    hideController()
                }
                playerView
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Black)
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}


