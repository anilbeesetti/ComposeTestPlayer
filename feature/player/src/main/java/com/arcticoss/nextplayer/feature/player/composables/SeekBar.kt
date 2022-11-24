package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Custom seek bar for the player controls
 */
@Composable
fun SeekBar(
    positionMs: Long,
    durationMs: Long,
    onScrubStart: (() -> Unit)?,
    onScrubMove: (positionMs: Long) -> Unit,
    onScrubStop: ((positionMs: Long) -> Unit)?,
    modifier: Modifier = Modifier
) {
    var scrubbing by remember { mutableStateOf(false) }
    val duration = if(durationMs < 0) 0F else durationMs.toFloat()
    var scrubPosition by remember { mutableStateOf(positionMs) }
    val currentPosition by rememberUpdatedState(positionMs)
    val scope = rememberCoroutineScope()

    val headPosition by remember { derivedStateOf { if (scrubbing) scrubPosition else currentPosition } }

    Slider(
        value = headPosition.toFloat(),
        onValueChange = {
            scrubPosition = it.toLong()
            if (!scrubbing) {
                onScrubStart?.invoke()
                scrubbing = true
            } else {
                onScrubMove.invoke(it.toLong())
            }
        },
        onValueChangeFinished = {
            onScrubStop?.invoke(scrubPosition)
            scope.launch {
                delay(1000)
                scrubbing = false
            }
        },
        modifier = modifier,
        valueRange = 0F..duration,
    )
}

