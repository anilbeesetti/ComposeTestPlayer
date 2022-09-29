package com.arcticoss.nextplayer.player.ui.playerscreen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File


private const val TAG = "NextPlayerScreen"

@Composable
fun NextPlayerScreen(
    mediaPath: String,
    exoPlayer: ExoPlayer,
    onVisibilityChange: (visibility: Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NextExoPlayer(exoPlayer = exoPlayer, mediaPath = mediaPath, onVisibilityChange = onVisibilityChange)
        NextPlayerUI(mediaPath, player = exoPlayer, onBackPressed = {})
    }
}

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackPressed() }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "", tint = Color.White)
            }
            Text(text = file.name, color = Color.White)
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
//            IconButton(
//                onClick = {
//                    isPlaying = if (player.isPlaying) {
//                        player.pause()
//                        false
//                    } else {
//                        player.play()
//                        true
//                    }
//                },
//            ) {
//                Icon(
//                    imageVector = if (isPlaying) Icons.Rounded.Star else Icons.Rounded.PlayArrow,
//                    contentDescription = "", tint = Color.White,
//                )
//            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        isPlaying = if (player.isPlaying) {
                            player.pause()
                            false
                        } else {
                            player.play()
                            true
                        }
                    }
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "", tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "00:00:00", color = Color.White)
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Slider(value = 0F, onValueChange = {})
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "03:00:00", color = Color.White)
        }
    }
}


@Composable
fun NextExoPlayer(
    exoPlayer: ExoPlayer,
    mediaPath: String,
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
                    hideController()
                    useController = false
                    player = exoPlayer
                    setControllerVisibilityListener(
                        StyledPlayerView.ControllerVisibilityListener { onVisibilityChange(it) }
                    )
                }
                playerView
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}