package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "width") val width: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "frame_rate") val frameRate: Double,
    @ColumnInfo(name = "added_on") val addedOn: Long,
    @ColumnInfo(name = "last_played_on") val lastPlayedOn: Long? = null,
    @ColumnInfo(name = "last_played_position") val lastPlayedPosition: Long = 0,
    @ColumnInfo(name = "audio_track_id") val audioTrackId: String? = null,
    @ColumnInfo(name = "subtitle_track_id") val subtitleTrackId: String? = null,
    @ColumnInfo(name = "folder_id") val folderId: Long
)
