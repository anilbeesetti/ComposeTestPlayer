package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.PlayerUIFooter
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.PlayerUIHeader
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.PlayerUIMainControls
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
    var seekPosition by remember {
        mutableStateOf(0f)
    }
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
                    seekPosition = it
                    player.seekTo(seekPosition.toLong())
                },
                onSeekCompleted = {
                }
            )
        }
        PlayerUIMainControls(
            isPlaying = playerState.isPlaying,
            onPlayPauseClick = {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


