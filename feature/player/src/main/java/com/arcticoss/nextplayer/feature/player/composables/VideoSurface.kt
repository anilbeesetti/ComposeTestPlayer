package com.arcticoss.nextplayer.feature.player.composables

import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.Player

@Composable
fun VideoSurface(
    player: Player,
    surfaceType: SurfaceType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun Player.clearVideoView(view: View) {
        when(surfaceType) {
            SurfaceType.SurfaceView -> clearVideoSurfaceView(view as SurfaceView)
            SurfaceType.TextureView -> clearVideoTextureView(view as TextureView)
        }
    }

    fun Player.setVideoView(view: View) {
        when (surfaceType) {
            SurfaceType.SurfaceView -> setVideoSurfaceView(view as SurfaceView)
            SurfaceType.TextureView -> setVideoTextureView(view as TextureView)
        }
    }

    val videoView = remember {
        when (surfaceType) {
            SurfaceType.SurfaceView -> SurfaceView(context)
            SurfaceType.TextureView -> TextureView(context)
        }
    }

    AndroidView(
        factory = { videoView },
        modifier = modifier
    ) {
        val previousPlayer = it.tag as? Player
        if (previousPlayer == player) return@AndroidView

        previousPlayer?.clearVideoView(it)
        it.tag = player.apply { setVideoView(it) }
    }

    DisposableEffect(Unit) {
        onDispose {
            (videoView.tag as? Player)?.clearVideoView(videoView)
        }
    }
}


enum class SurfaceType {
    SurfaceView,
    TextureView;
}