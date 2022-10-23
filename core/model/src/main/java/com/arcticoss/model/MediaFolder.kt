package com.arcticoss.model

data class MediaFolder(
    val id: Long = 0,
    val name: String = "",
    val path: String = "",
    val mediaItems: List<MediaItem> = emptyList()
)