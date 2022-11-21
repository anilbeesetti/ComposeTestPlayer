package com.arcticoss.nextplayer.feature.player.compose

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.arcticoss.nextplayer.feature.player.presentation.ControllerState
import com.arcticoss.nextplayer.feature.player.presentation.ControllerVisibility
import com.arcticoss.nextplayer.feature.player.presentation.MediaState


private const val TAG = "MediaGestures"

@Composable
fun MediaGestures(
    mediaState: MediaState,
    controller: ControllerState
) {
    Box(
        modifier = Modifier
            .systemGesturesPadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        mediaState.controllerVisibility = when (mediaState.controllerVisibility) {
                            ControllerVisibility.Visible -> ControllerVisibility.Invisible
                            ControllerVisibility.PartiallyVisible -> ControllerVisibility.Visible
                            ControllerVisibility.Invisible -> ControllerVisibility.Visible
                        }
                    },
                    onDoubleTap = { controller.playOrPause() }
                )
            }
    )
}