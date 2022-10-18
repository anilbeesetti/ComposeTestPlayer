package com.arcticoss.data.utils

import com.arcticoss.database.entities.AudioTrackEntity
import com.arcticoss.database.entities.MediaItemEntity
import com.arcticoss.database.entities.SubtitleTrackEntity
import com.arcticoss.database.entities.VideoTrackEntity
import com.arcticoss.mediainfo.models.AudioStream
import com.arcticoss.mediainfo.models.MediaInfo
import com.arcticoss.mediainfo.models.SubtitleStream
import com.arcticoss.mediainfo.models.VideoStream


fun MediaInfo.asMediaItemEntity(folderId: Long) =
    MediaItemEntity(
        title = this.title,
        size = this.size,
        path = this.filePath,
        width = this.width,
        duration = this.duration,
        height = this.height,
        frameRate = this.frameRate,
        folderId = folderId
    )

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
        mediaItemId = mediaItemId
    )

fun AudioStream.asAudioTrackEntity(mediaItemId: Long) =
    AudioTrackEntity(
        streamIndex = this.index,
        codec = this.codecName,
        sampleRate = this.sampleRate,
        channels = this.channels,
        bitrate = this.bitRate,
        language = this.language,
        mediaItemId = mediaItemId
    )

fun SubtitleStream.asSubtitleTrackEntity(mediaItemId: Long) =
    SubtitleTrackEntity(
        streamIndex = this.index,
        codec = this.codecName,
        language = this.language,
        mediaItemId = mediaItemId
    )