package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BrightnessAdjustmentBar(
    brightness: Int,
    maxBrightness: Int,
    modifier: Modifier = Modifier
) {
    AdjustmentBar(
        icon = Icons.Rounded.LightMode,
        value = brightness,
        maxValue = maxBrightness,
        modifier = modifier
    )
}

@Composable
fun AudioAdjustmentBar(
    volumeLevel: Int,
    maxVolumeLevel: Int,
    modifier: Modifier = Modifier
) {
    AdjustmentBar(
        value = volumeLevel,
        maxValue = maxVolumeLevel,
        icon = if (volumeLevel == 0) Icons.Rounded.VolumeMute else Icons.Rounded.VolumeUp,
        modifier = modifier
    )
}