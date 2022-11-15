package com.arcticoss.feature.player.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.hideSystemBars() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Activity.showSystemBars() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        show(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}


fun Activity.setBrightness(brightness: Float) {
    val windowAttributes = this.window.attributes
    windowAttributes.screenBrightness = brightness
    this.window.attributes = windowAttributes
}

@SuppressLint("SourceLockedOrientationActivity")
fun Activity.setOrientation(orientation: Orientation, onSuccess: (Orientation) -> Unit) {
    when(orientation) {
        Orientation.PORTRAIT -> this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Orientation.PORTRAIT_SENSOR -> this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        Orientation.LANDSCAPE -> this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Orientation.LANDSCAPE_SENSOR -> this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }
    onSuccess(orientation)
}

enum class Orientation {
    PORTRAIT,
    PORTRAIT_SENSOR,
    LANDSCAPE,
    LANDSCAPE_SENSOR
}