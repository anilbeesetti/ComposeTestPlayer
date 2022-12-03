package com.arcticoss.nextplayer.core.data.utils

import com.arcticoss.nextplayer.core.database.entities.AudioTrackEntity
import com.arcticoss.nextplayer.core.database.entities.SubtitleTrackEntity
import com.arcticoss.nextplayer.core.database.entities.VideoTrackEntity
import com.arcticoss.nextplayer.mediainfo.models.AudioStream
import com.arcticoss.nextplayer.mediainfo.models.SubtitleStream
import com.arcticoss.nextplayer.mediainfo.models.VideoStream


fun VideoStream.asVideoTrackEntity(mediaItemId: Long) =
    VideoTrackEntity(
        streamIndex = this.index,
        width = this.frameWidth,
        height = this.frameHeight,
        frameRate = this.frameRate,
        bitrate = this.bitRate,
        codec = this.codecName,
        title = this.title,
        language = this.language,
        mediaId = mediaItemId
    )

fun AudioStream.asAudioTrackEntity(mediaItemId: Long) =
    AudioTrackEntity(
        streamIndex = this.index,
        codec = this.codecName,
        sampleRate = this.sampleRate,
        channels = this.channels,
        bitrate = this.bitRate,
        language = this.language,
        mediaId = mediaItemId
    )

fun SubtitleStream.asSubtitleTrackEntity(mediaItemId: Long) =
    SubtitleTrackEntity(
        streamIndex = this.index,
        codec = this.codecName,
        language = this.language,
        mediaId = mediaItemId
    )