package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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