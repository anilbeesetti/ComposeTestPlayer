package com.arcticoss.nextplayer.core.model

data class Folder(
    val id: Long = 0,
    val name: String = "",
    val path: String = "",
    val mediaList: List<Media> = emptyList()
)