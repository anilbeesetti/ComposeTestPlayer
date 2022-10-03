package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.NextExoPlayer
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.NextPlayerUI
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay


private const val TAG = "NextPlayerScreen"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextPlayerScreen(
    mediaPath: String,
    player: ExoPlayer,
    viewModel: NextPlayerViewModel = viewModel(),
    onVisibilityChange: (visibility: Boolean) -> Unit,
    onBackPressed: () -> Unit
) {
    var showUI by remember {
        mutableStateOf(false)
    }
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = showUI, key2 = playerState.isPlaying) {
        if (playerState.isPlaying) {
            if (showUI) {
                delay(5000)
                showUI = false
                onVisibilityChange(false)
            } else {
                onVisibilityChange(false)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showUI = !showUI
                onVisibilityChange(showUI)
            }
    ) {
        NextExoPlayer(
            exoPlayer = player,
            mediaPath = mediaPath,
            viewModel = viewModel,
            onBackPressed = onBackPressed
        )
        AnimatedVisibility(
            visible = showUI,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                NextPlayerUI(
                    mediaPath,
                    player = player,
                    onBackPressed = onBackPressed,
                    viewModel = viewModel
                )
            }
        }
    }
}