package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TimeAndSeekbar(
    positionMs: Long,
    durationMs: Long,
    modifier: Modifier = Modifier,
    onScrubStart: (() -> Unit)?,
    onScrubMove: (positionMs: Long) -> Unit,
    onScrubStop: ((positionMs: Long) -> Unit)?,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeText(time = positionMs)
        SeekBar(
            durationMs = durationMs,
            positionMs = positionMs,
            onScrubStart = onScrubStart,
            onScrubMove = onScrubMove,
            onScrubStop = onScrubStop,
            modifier = Modifier
                .weight(1f),
        )
        TimeText(time = durationMs)
    }
}

@Composable
private fun TimeText(
    time: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Text(
        text = TimeUtils.formatTime(context, time),
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(horizontal = 5.dp)
    )
}


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
    val duration = if (durationMs < 0) 0F else durationMs.toFloat()
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

