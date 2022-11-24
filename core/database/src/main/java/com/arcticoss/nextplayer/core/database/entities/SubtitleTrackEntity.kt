package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


/**
 * Defines a subtitle_track for [MediaEntity]
 * It has one to many relationship with [MediaEntity]
 */
@Entity(
    primaryKeys = ["media_id", "stream_index"],
    tableName = "subtitle_track",
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
data class SubtitleTrackEntity(
    @ColumnInfo(name = "stream_index") val streamIndex: Int,
    @ColumnInfo(name = "codec") val codec: String,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "media_id") val mediaId: Long
)
