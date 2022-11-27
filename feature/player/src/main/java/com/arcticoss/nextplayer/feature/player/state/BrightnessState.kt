package com.arcticoss.nextplayer.feature.player.state

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arcticoss.nextplayer.feature.player.utils.swipeToShowStatusBars

@Composable
fun rememberBrightnessState(
    activity: Activity?,
    maxBrightness: Int = 25,
): BrightnessState = remember { BrightnessState(maxBrightness, activity) }

@Stable
class BrightnessState(
    maxBrightness: Int,
    private val activity: Activity? = null
) {

    /**
     * Get the max brightness
     */
    var maxBrightness: Int by mutableStateOf(maxBrightness)
        private set

    /**
     * Get current brightness
     */
    var currentBrightness: Int by mutableStateOf(0)
        private set

    /**
     * Increase brightness
     */
    fun increaseBrightness() {
        if ((currentBrightness + 1) <= maxBrightness) {
            val level = 1.0f / maxBrightness * (currentBrightness + 1)
            activity?.setBrightness(level)
            currentBrightness++
        }
    }

    /**
     * Decrease brightness
     */
    fun decreaseBrightness() {
        if ((currentBrightness - 1) >= 0) {
            val level = 1.0f / maxBrightness * (currentBrightness - 1)
            activity?.setBrightness(level)
            currentBrightness--
        }
    }

    /**
     * Set brightness
     */
    fun setBrightness(value: Int) {
        if (value in 0..25) {
            val level = 1.0f / maxBrightness * value
            activity?.setBrightness(level)
            currentBrightness = value
        }
    }
}


fun Activity.setBrightness(brightness: Float) {
    val windowAttributes = this.window.attributes
    windowAttributes.screenBrightness = brightness
    this.window.attributes = windowAttributes
    swipeToShowStatusBars()
}