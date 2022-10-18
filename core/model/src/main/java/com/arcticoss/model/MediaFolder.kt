package com.arcticoss.model

data class MediaFolder(
    val id: Long,
    val name: String,
    val mediaItems: List<MediaItem>
)