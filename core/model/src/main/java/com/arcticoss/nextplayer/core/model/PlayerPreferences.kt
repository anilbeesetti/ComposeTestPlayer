package com.arcticoss.nextplayer.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerPreferences(
    val saveBrightnessLevel: Boolean = true,
    val savePlayBackSpeed: Boolean = false,
    val brightnessLevel: Int = 15,
    val resume: Resume = Resume.Always,
    val playbackSpeed: Int = 100,
    val fastSeeking: Boolean = true,
    val aspectRatio: AspectRatio = AspectRatio.FitScreen
)


enum class Resume(val title: String) {
    Always(title = "Always"),
    Never(title = "Never"),
    Ask(title = "Ask at startup")
}

enum class AspectRatio(val title: String) {
    FitScreen(title = "Fit to Screen"),
    FixedWidth(title = "Fixed width"),
    FixedHeight(title = "Fixed height"),
    Fill(title = "Fill"),
    Zoom(title = "Zoom")
}

inline fun <reified T: Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}