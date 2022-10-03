package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.player.ui.playerscreen.NextPlayerViewModel
import com.google.android.exoplayer2.ExoPlayer
import java.io.File


private const val TAG = "NextPlayerUI"

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextPlayerUI(
    path: String,
    player: ExoPlayer,
    viewModel: NextPlayerViewModel,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val file = File(path)
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            PlayerUIHeader(title = file.name, onBackPressed = onBackPressed)
            PlayerUIFooter(
                duration = duration,
                currentPosition = playerState.currentPosition,
                onSeek = {
                    viewModel.updateCurrentPosition(it.toLong())
                    player.seekTo(it.toLong())
                }
            )
        }
        PlayerUIMainControls(
            isPlaying = playerState.playWhenReady,
            onPlayPauseClick = {
                if (player.playWhenReady) {
                    player.pause()
                } else {
                    player.play()
                }
            },
            onSkipNextClick = { player.seekToNext() },
            onSkipPreviousClick = { player.seekToPrevious() },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


