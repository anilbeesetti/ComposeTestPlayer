package com.arcticoss.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Defines a subtitle_track for [MediaItemEntity]
 * It has one to many relationship with [MediaItemEntity]
 */
@Entity(
    tableName = "subtitle_track",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_item_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubtitleTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "stream_index") val streamIndex: Int,
    @ColumnInfo(name = "codec") val codec: String,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "media_item_id") val mediaItemId: Long
)
