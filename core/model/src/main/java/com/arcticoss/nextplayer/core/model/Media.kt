package com.arcticoss.nextplayer.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil

data class Media(
    val id: Long = 0,
    val size: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val path: String = "",
    val title: String = "",
    val duration: Long? = 0,
    val frameRate: Double = 0.0,
    val thumbnailPath: String = "",
    val addedOn: Long = 0,
    val lastPlayedOn: Long? = null,
    val lastPlayedPosition: Long = 0,
    val audioTrackId: String? = null,
    val subtitleTrackId: String? = null,
    val localSubtitleTracks: List<LocalSub> = emptyList(),
    val videoTracks: List<VideoTrack> = emptyList(),
    val audioTracks: List<AudioTrack> = emptyList(),
    val subtitleTracks: List<SubtitleTrack> = emptyList()
) {

    val noOfDaysSinceAdded: Int
        get() {
            val instant = Instant.fromEpochMilliseconds(addedOn)
            return instant.daysUntil(Clock.System.now(), TimeZone.currentSystemDefault())
        }

    val isWatchingCompleted: Boolean
        get() = duration != null && lastPlayedPosition >= duration && lastPlayedOn != null
}


data class VideoTrack(
    val streamIndex: Int,
    val bitrate: Long,
    val title: String?,
    val codec: String,
    val language: String?,
    val frameRate: Double,
)

data class SubtitleTrack(
    val streamIndex: Int,
    val codec: String,
    val language: String?,
)

data class AudioTrack(
    val streamIndex: Int,
    val codec: String,
    val sampleRate: Int,
    val channels: Int,
    val bitrate: Long,
    val language: String?,
)

data class LocalSub(
    val path: String,
    val language: String?,
    val selected: Boolean
)