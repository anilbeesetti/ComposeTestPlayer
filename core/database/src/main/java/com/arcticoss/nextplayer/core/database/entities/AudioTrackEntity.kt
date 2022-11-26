package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.arcticoss.nextplayer.core.model.AudioTrack


/**
 * Defines a audio_track for [MediaEntity]
 * It has one to many relationship with [MediaEntity]
 */
@Entity(
    primaryKeys = ["media_id", "stream_index"],
    tableName = "audio_track",
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
data class AudioTrackEntity(
    @ColumnInfo(name = "stream_index") val streamIndex: Int,
    @ColumnInfo(name = "audio_codec") val codec: String,
    @ColumnInfo(name = "sample_rate") val sampleRate: Int,
    @ColumnInfo(name = "channels") val channels: Int,
    @ColumnInfo(name = "bitrate") val bitrate: Long,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "media_id") val mediaId: Long
)


fun AudioTrackEntity.asExternalModel() = AudioTrack(
    streamIndex = streamIndex,
    codec = codec,
    sampleRate = sampleRate,
    channels = channels,
    bitrate = bitrate,
    language = language
)
