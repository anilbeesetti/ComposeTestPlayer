package com.arcticoss.nextplayer.player.utils

import android.app.Activity

object BrightnessController {

    const val MAX_BRIGHTNESS = 30

    fun increaseBrightness(activity: Activity, currentBrightness: Int, onBrightnessChanged: (Int) -> Unit) {
        val newBrightness = currentBrightness + 1
        if (newBrightness <= MAX_BRIGHTNESS) {
            changeBrightness(activity, newBrightness)
            onBrightnessChanged(newBrightness)
        }
    }

    fun decreaseBrightness(activity: Activity, currentBrightness: Int, onBrightnessChanged: (Int) -> Unit) {
        val newBrightness = currentBrightness - 1
        if (newBrightness >= 0) {
            changeBrightness(activity, newBrightness)
            onBrightnessChanged(newBrightness)
        }
    }

    fun setBrightness(activity: Activity, newBrightness: Int) {
        changeBrightness(activity, newBrightness)
    }

    private fun changeBrightness(activity: Activity, newBrightness: Int) {
        val windowAttributes = activity.window.attributes
        val level = 1.0f / MAX_BRIGHTNESS * newBrightness
        windowAttributes.screenBrightness = (level * level)
        activity.window.attributes = windowAttributes
    }
}