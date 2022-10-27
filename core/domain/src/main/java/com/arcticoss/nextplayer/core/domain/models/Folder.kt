package com.arcticoss.nextplayer.core.domain.models


data class Folder(
    val id: Long = 0,
    val name: String = "",
    val path: String = "",
    val mediaItemCount: Int = 0
)