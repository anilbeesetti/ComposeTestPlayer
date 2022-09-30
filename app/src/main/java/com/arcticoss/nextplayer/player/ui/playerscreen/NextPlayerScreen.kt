package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer


private const val TAG = "NextPlayerScreen"

@Composable
fun NextPlayerScreen(
    mediaPath: String,
    exoPlayer: ExoPlayer,
    viewModel: NextPlayerViewModel = viewModel(),
    onVisibilityChange: (visibility: Int) -> Unit,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NextExoPlayer(
            exoPlayer = exoPlayer,
            mediaPath = mediaPath,
            viewModel = viewModel,
            onVisibilityChange = onVisibilityChange
        )
        NextPlayerUI(mediaPath, player = exoPlayer, onBackPressed = onBackPressed)
    }
}