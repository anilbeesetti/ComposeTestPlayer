package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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
        icon = if (volumeLevel == 0) Icons.Rounded.VolumeMute else Icons.Rounded.VolumeUp,
        value = volumeLevel,
        maxValue = maxVolumeLevel,
        modifier = modifier
    )
}


@Composable
fun AdjustmentBar(
    icon: ImageVector,
    value: Int,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    val progress = (1f / maxValue) * value
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.extraSmall)
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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