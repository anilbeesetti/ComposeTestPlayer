package com.arcticoss.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Defines a video_track for [MediaItemEntity]
 * It has one to many relationship with [MediaItemEntity]
 */
@Entity(
    tableName = "video_track",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_item_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VideoTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "stream_index") val streamIndex: Int,
    @ColumnInfo(name = "width") val width: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "bitrate") val bitrate: Long,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "video_codec") val codec: String,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "frame_rate") val frameRate: Double,
    @ColumnInfo(name = "media_item_id") val mediaItemId: Long
)
