package com.arcticoss.mediainfo.models

data class SubtitleStream(
    val index: Int,
    val title: String?,
    val codecName: String,
    val language: String?
)
