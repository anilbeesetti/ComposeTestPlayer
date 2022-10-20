package com.arcticoss.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaItem(
    val id: Long = 0,
    val size: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val path: String = "",
    val title: String = "",
    val frameRate: Double = 0.0,
    val duration: Long = 0,
    val lastPlayedPosition: Long = 0,
    val thumbnailPath: String? = null
)
