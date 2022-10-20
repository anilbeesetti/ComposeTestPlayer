package com.arcticoss.feature.player

fun PlayerViewModel.increaseVolume() {
    this.onEvent(PlayerEvent.IncreaseVolume)
}

fun PlayerViewModel.decreaseVolume() {
    this.onEvent(PlayerEvent.DecreaseVolume)
}