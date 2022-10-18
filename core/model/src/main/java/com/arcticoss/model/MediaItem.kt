package com.arcticoss.model

import java.time.Duration

data class MediaItem(
    val id: Long,
    val size: Long,
    val width: Int,
    val height: Int,
    val path: String,
    val title: String,
    val frameRate: Double,
    val duration: Long,
    val lastPlayedPosition: Long
)
