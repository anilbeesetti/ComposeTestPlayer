package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.exoplayer2.ExoPlayer


private const val TAG = "NextPlayerScreen"

@Composable
fun NextPlayerScreen(
    showUI: Boolean,
    mediaPath: String,
    exoPlayer: ExoPlayer,
    viewModel: NextPlayerViewModel = viewModel(),
    onVisibilityChange: (visibility: Boolean) -> Unit,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onVisibilityChange(!showUI)
            }
    ) {
        NextExoPlayer(
            exoPlayer = exoPlayer,
            mediaPath = mediaPath,
            viewModel = viewModel
        )
        AnimatedVisibility(
            visible = showUI,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                NextPlayerUI(
                    mediaPath,
                    player = exoPlayer,
                    onBackPressed = onBackPressed,
                    viewModel = viewModel
                )
            }
        }
    }
}