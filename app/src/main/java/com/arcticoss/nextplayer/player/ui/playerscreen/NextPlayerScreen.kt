package com.arcticoss.nextplayer.player.ui.playerscreen

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.LinearVerticalProgressIndicator
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.NextExoPlayer
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.NextPlayerUI
import com.arcticoss.nextplayer.player.utils.BrightnessController
import com.arcticoss.nextplayer.player.utils.findActivity
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.delay
import kotlin.math.abs


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
    var dragStartOffset by remember {
        mutableStateOf(0.0F)
    }
    var playerCurrentState by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
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
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showUI = !showUI
                        onVisibilityChange(showUI)
                    },
                    onDoubleTap = {
                        if (player.playWhenReady) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        playerCurrentState = player.playWhenReady
                        player.playWhenReady = false
                        dragStartOffset = it.x
                    },
                    onHorizontalDrag = { change: PointerInputChange, dragAmount: Float ->
                        val seekAmount = abs(change.position.x - dragStartOffset) * dragAmount
                        viewModel.updateCurrentPosition(
                            (playerState.currentPosition + seekAmount.toLong()).coerceIn(
                                0..playerState.currentMediaItemDuration
                            )
                        )
                        player.seekTo(playerState.currentPosition)
                        dragStartOffset = change.position.x
                    },
                    onDragEnd = {
                        player.playWhenReady = playerCurrentState
                    }
                )
            }
            .pointerInput(Unit) {
                val activity = context.findActivity()
                val width = activity?.resources?.displayMetrics?.widthPixels ?: 0
                val height = activity?.resources?.displayMetrics?.heightPixels ?: 0
                var initialOffset = 0.0f
                var currentMetricChange = "Audio"
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        initialOffset = offset.y
                        currentMetricChange = if (offset.x < (width / 2)) {
                            "Brightness"
                        } else {
                            "Audio"
                        }
                    },
                    onVerticalDrag = { change: PointerInputChange, dragAmount: Float ->
                        if (abs(change.position.y - initialOffset) > height / 18) {
                            if (currentMetricChange == "Audio") {
                                if (change.position.y - initialOffset < 0) {
                                    audioManager.adjustStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FLAG_PLAY_SOUND
                                    )
                                } else {
                                    audioManager.adjustStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_LOWER,
                                        AudioManager.FLAG_PLAY_SOUND
                                    )
                                }
                                viewModel.updateVolumeLevel(
                                    audioManager.getStreamVolume(
                                        AudioManager.STREAM_MUSIC
                                    )
                                )
                            } else {
                                activity?.let {
                                    if (change.position.y - initialOffset < 0) {
                                        BrightnessController.increaseBrightness(
                                            it,
                                            playerState.currentBrightness,
                                            onBrightnessChanged = { newBrightness ->
                                                viewModel.updateBrightness(newBrightness)
                                            }
                                        )
                                    } else {
                                        BrightnessController.decreaseBrightness(
                                            it,
                                            playerState.currentBrightness,
                                            onBrightnessChanged = { newBrightness ->
                                                viewModel.updateBrightness(newBrightness)
                                            }
                                        )
                                    }
                                }
                            }
                            initialOffset = change.position.y
                        }
                    }
                )
            }
    ) {
        NextExoPlayer(
            exoPlayer = player,
            mediaPath = mediaPath,
            viewModel = viewModel,
            onBackPressed = onBackPressed,
            changeOrientation = { requestedOrientation ->
                val activity = context.findActivity()
                activity?.requestedOrientation = requestedOrientation
                activity?.requestedOrientation?.let { viewModel.updateScreenOrientation(it) }
            }
        )
        VerticalSwipeMediaControls(
            volumeLevel = playerState.currentVolumeLevel,
            brightness = playerState.currentBrightness,
            maxVolumeLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            maxBrightness = BrightnessController.MAX_BRIGHTNESS,
            modifier = Modifier.align(Alignment.Center)
        )
        AnimatedVisibility(
            visible = showUI,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NextPlayerUI(
                mediaPath,
                player = player,
                onBackPressed = onBackPressed,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun AdjustmentBar(
    value: Int,
    maxValue: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.extraSmall)
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val progress = (1f / maxValue) * value
        Text(text = value.toString())
        Spacer(modifier = Modifier.height(10.dp))
        LinearVerticalProgressIndicator(
            modifier = Modifier.weight(1f),
            progress = progress
        )
        Spacer(modifier = Modifier.height(10.dp))
        Icon(imageVector = icon, contentDescription = icon.name)
    }
}


@Composable
fun VerticalSwipeMediaControls(
    volumeLevel: Int,
    brightness: Int,
    maxVolumeLevel: Int,
    maxBrightness: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AdjustmentBar(
            icon = Icons.Rounded.LightMode,
            value = brightness,
            maxValue = maxBrightness
        )
        AdjustmentBar(
            value = volumeLevel,
            maxValue = maxVolumeLevel,
            icon = if (volumeLevel == 0) Icons.Rounded.VolumeMute else Icons.Rounded.VolumeUp
        )
    }
}