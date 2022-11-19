package com.arcticoss.nextplayer.feature.player.utils

import android.media.AudioManager


fun AudioManager.increaseVolume() {
    this.adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        AudioManager.ADJUST_RAISE,
        AudioManager.FLAG_PLAY_SOUND
    )
}

fun AudioManager.decreaseVolume() {
    this.adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        AudioManager.ADJUST_LOWER,
        AudioManager.FLAG_PLAY_SOUND
    )
}

fun AudioManager.getVolume(): Int {
    return this.getStreamVolume(
        AudioManager.STREAM_MUSIC
    )
}

fun AudioManager.setVolume(value: Int) {
    this.setStreamVolume(
        AudioManager.STREAM_MUSIC,
        value,
        AudioManager.FLAG_PLAY_SOUND
    )
}