package com.arcticoss.nextplayer.player.ui.playerscreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


private const val TAG = "NextPlayerScreen"

@Composable
fun NextPlayerScreen(
    mediaPath: String,
    exoPlayer: ExoPlayer,
    viewModel: NextPlayerViewModel = viewModel(),
    onVisibilityChange: (visibility: Int) -> Unit
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
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }
            Text(text = file.name, color = Color.White)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
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


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NextExoPlayer(
    exoPlayer: ExoPlayer,
    mediaPath: String,
    viewModel: NextPlayerViewModel,
    onVisibilityChange: (visibility: Int) -> Unit
) {
    val lastPlayedPosition by viewModel.lastPlayedPosition.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    LaunchedEffect(exoPlayer) {
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(mediaPath)))
        exoPlayer.addMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.seekTo(lastPlayedPosition)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    AddLifecycleEventObserver(
        lifecycleOwner = lifecycleOwner,
        onLifecycleEvent = { event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.setIsPlaying(exoPlayer.playWhenReady)
                    exoPlayer.playWhenReady = false
                    viewModel.setLastPlayingPosition(exoPlayer.currentPosition)
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = isPlaying
                }
                Lifecycle.Event.ON_START -> { }
                else -> {}
            }
        }
    )


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

@Composable
fun AddLifecycleEventObserver(
    lifecycleOwner: LifecycleOwner,
    onLifecycleEvent: (Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            onLifecycleEvent(event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}