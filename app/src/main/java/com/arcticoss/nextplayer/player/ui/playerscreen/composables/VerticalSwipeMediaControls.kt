package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.player.ui.playerscreen.PlayerUiEvent
import kotlinx.coroutines.delay

@Composable
fun VerticalSwipeMediaControls(
    volumeLevel: Int,
    brightness: Int,
    maxVolumeLevel: Int,
    maxBrightness: Int,
    showVolumeBar: Boolean,
    showBrightnessBar: Boolean,
    dismissVolumeBar: () -> Unit,
    dismissBrightnessBar: () -> Unit,
    modifier: Modifier = Modifier
) {

//    LaunchedEffect(key1 = showBrightnessBar, key2 = brightness) {
//        if (showBrightnessBar) {
//            dismissVolumeBar()
//            delay(1000)
//            dismissBrightnessBar()
//        }
//    }
//
//    LaunchedEffect(key1 = showVolumeBar, key2 = volumeLevel) {
//        if (showVolumeBar) {
//            dismissBrightnessBar()
//            delay(1000)
//            dismissVolumeBar()
//        }
//    }

    Row(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AdjustmentBar(
            value = volumeLevel,
            maxValue = maxVolumeLevel,
            icon = if (volumeLevel == 0) Icons.Rounded.VolumeMute else Icons.Rounded.VolumeUp,
            modifier = Modifier.alpha(if (showVolumeBar) 1f else 0f)
        )
        AdjustmentBar(
            icon = Icons.Rounded.LightMode,
            value = brightness,
            maxValue = maxBrightness,
            modifier = Modifier.alpha(if (showBrightnessBar) 1f else 0f)
        )
    }
}