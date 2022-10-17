package com.arcticoss.mediainfo.models

data class VideoStream(
    val index: Int,
    val title: String?,
    val codecName: String,
    val language: String?,
    val bitRate: BitRate,
    val frameRate: FrameRate,
    val frameWidth: Int,
    val frameHeight: Int
)

typealias BitRate = Long
typealias FrameRate = Double
