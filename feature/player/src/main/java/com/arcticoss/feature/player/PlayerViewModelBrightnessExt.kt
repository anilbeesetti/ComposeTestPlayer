package com.arcticoss.feature.player

fun PlayerViewModel.increaseBrightness() {
    this.showBrightnessBar()
    val brightness = playerState.value.brightnessLevel
    if (brightness < 25) {
        this.setBrightness(brightness + 1)
    }
}

fun PlayerViewModel.decreaseBrightness() {
    this.showBrightnessBar()
    val brightness = playerState.value.brightnessLevel
    if (brightness > 0) {
        this.setBrightness(brightness - 1)
    }
}

fun PlayerViewModel.setBrightness(level: Int) {
    this.onEvent(PlayerEvent.SetBrightness(level))
}

fun PlayerViewModel.showBrightnessBar() {
    this.onUiEvent(PlayerUiEvent.ShowBrightnessBar(true))
}

fun PlayerViewModel.hideBrightnessBar() {
    this.onUiEvent(PlayerUiEvent.ShowBrightnessBar(false))
}