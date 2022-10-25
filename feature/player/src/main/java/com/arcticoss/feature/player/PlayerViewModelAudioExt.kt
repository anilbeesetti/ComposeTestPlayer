package com.arcticoss.feature.player

fun PlayerViewModel.increaseVolume() {
    val volume = playerState.value.volumeLevel
    if (volume < 25) {
        this.setVolume(volume + 1)
    }
}

fun PlayerViewModel.decreaseVolume() {
    val volume = playerState.value.volumeLevel
    if (volume > 0) {
        this.setVolume(volume - 1)
    }
}

fun PlayerViewModel.setVolume(level: Int) {
    this.onEvent(PlayerEvent.SetVolume(level))
}

fun PlayerViewModel.showVolumeBar() {
    this.onUiEvent(PlayerUiEvent.ShowVolumeBar(true))
}

fun PlayerViewModel.hideVolumeBar() {
    this.onUiEvent(PlayerUiEvent.ShowVolumeBar(false))
}