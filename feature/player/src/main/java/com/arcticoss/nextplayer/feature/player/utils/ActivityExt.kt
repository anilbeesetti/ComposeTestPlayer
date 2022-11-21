package com.arcticoss.nextplayer.feature.player.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Hide system bars
 */
fun Activity.hideSystemBars() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
    }
}

/**
 * Show system bars
 */
fun Activity.showSystemBars() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        show(WindowInsetsCompat.Type.systemBars())
    }
}


fun Activity.setBrightness(brightness: Float) {
    val windowAttributes = this.window.attributes
    windowAttributes.screenBrightness = brightness
    this.window.attributes = windowAttributes
    swipeToShowStatusBars()
}

fun Activity.keepScreenOn(value: Boolean) {
    when (value) {
        true -> {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        false -> {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    swipeToShowStatusBars()
}

/**
 * Must call this function after any configuration done to activity to keep system bars behaviour
 */
fun Activity.swipeToShowStatusBars() {
    WindowCompat.getInsetsController(window, window.decorView).systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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