package com.arcticoss.nextplayer.feature.player.composables

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun ExoPlayerView(
    player: Player,
    modifier: Modifier = Modifier
) {
    var playerView: StyledPlayerView? = null
    DisposableEffect(
        AndroidView(
            factory = { StyledPlayerView(it) },
            modifier = modifier
        ) {
            playerView = it
            val previousPlayer = it.tag as? Player
            if (previousPlayer == player) return@AndroidView
            it.player = player
            it.hideController()
            it.useController = false
            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            it.subtitleView?.visibility = View.GONE
        }) {

        onDispose {
            playerView?.player = null
            playerView = null
        }
    }
}