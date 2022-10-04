package com.arcticoss.nextplayer.player.ui.playerscreen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
                                Log.d(TAG, "NextPlayerScreen: audio")
                                //TODO: Audio
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