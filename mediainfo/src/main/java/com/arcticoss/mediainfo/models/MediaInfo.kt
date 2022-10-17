package com.arcticoss.mediainfo.models

data class MediaInfo(
    val fileFormatName: String,
    val duration: Long,
    val videoStreams: List<VideoStream>,
    val audioStreams: List<AudioStream>,
    val subtitleStreams: List<SubtitleStream>
)
