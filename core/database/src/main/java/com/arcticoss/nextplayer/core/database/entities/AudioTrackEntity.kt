package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Defines a audio_track for [MediaItemEntity]
 * It has one to many relationship with [MediaItemEntity]
 */
@Entity(
    tableName = "audio_track",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_item_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AudioTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "stream_index") val streamIndex: Int,
    @ColumnInfo(name = "audio_codec") val codec: String,
    @ColumnInfo(name = "sample_rate") val sampleRate: Int,
    @ColumnInfo(name = "channels") val channels: Int,
    @ColumnInfo(name = "bitrate") val bitrate: Long,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "media_item_id") val mediaItemId: Long
)
