package com.arcticoss.nextplayer.player.ui.playerscreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.PlayerUIFooter
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.PlayerUIHeader
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.PlayerUIMainControls
import com.google.android.exoplayer2.ExoPlayer
import java.io.File

@Composable
fun NextPlayerUI(
    path: String,
    player: ExoPlayer,
    onBackPressed: () -> Unit
) {
    val file = File(path)
    var isPlaying by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PlayerUIHeader(title = file.name, onBackPressed = onBackPressed)
        PlayerUIMainControls(
            isPlaying = isPlaying,
            onPlayPauseClick = {
                isPlaying = if (player.isPlaying) {
                    player.pause()
                    false
                } else {
                    player.play()
                    true
                }
            }
        )
        PlayerUIFooter()
    }
}


