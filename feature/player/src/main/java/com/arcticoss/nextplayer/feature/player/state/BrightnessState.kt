package com.arcticoss.nextplayer.feature.player.state

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arcticoss.nextplayer.feature.player.utils.setBrightness

@Composable
fun rememberBrightnessState(
    activity: Activity?,
    maxBrightness: Int = 25,
): BrightnessState = remember { BrightnessState(maxBrightness,activity) }

@Stable
class BrightnessState(
    maxBrightness: Int,
    private val activity: Activity? = null
) {

    var maxBrightness: Int by mutableStateOf(maxBrightness)

    var currentBrightness: Int by mutableStateOf(0)

    fun increaseBrightness() {
        if ((currentBrightness + 1) <= maxBrightness) {
            val level = 1.0f / maxBrightness * (currentBrightness + 1)
            activity?.setBrightness(level)
            currentBrightness++
        }
    }

    fun decreaseBrightness() {
        if ((currentBrightness - 1) >= 0f) {
            val level = 1.0f / maxBrightness * (currentBrightness - 1)
            activity?.setBrightness(level)
            currentBrightness--
        }
    }

}