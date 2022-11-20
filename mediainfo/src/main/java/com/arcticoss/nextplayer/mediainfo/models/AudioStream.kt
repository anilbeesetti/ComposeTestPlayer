package com.arcticoss.nextplayer.mediainfo.models

data class AudioStream(
    val index: Int,
    val title: String?,
    val codecName: String,
    val language: String?,
    val sampleRate: sampleRate,
    val bitRate: BitRate,
    val channels: Int,
    val channelLayout: String?,
)

typealias sampleRate = Int
