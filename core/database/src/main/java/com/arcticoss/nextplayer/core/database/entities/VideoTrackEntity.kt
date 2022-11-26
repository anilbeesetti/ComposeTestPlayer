package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.arcticoss.nextplayer.core.model.VideoTrack


/**
 * Defines a video_track for [MediaEntity]
 * It has one to many relationship with [MediaEntity]
 */
@Entity(
    primaryKeys = ["media_id", "stream_index"],
    tableName = "video_track",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["media_id"])]
)
data class VideoTrackEntity(
    @ColumnInfo(name = "stream_index") val streamIndex: Int,
    @ColumnInfo(name = "width") val width: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "bitrate") val bitrate: Long,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "video_codec") val codec: String,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "frame_rate") val frameRate: Double,
    @ColumnInfo(name = "media_id") val mediaId: Long
)



fun VideoTrackEntity.asExternalModel() = VideoTrack(
    streamIndex = streamIndex,
    bitrate = bitrate,
    title = title,
    codec = codec,
    language = language,
    frameRate = frameRate
)