package com.arcticoss.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "media")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "width") val width: Int,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "frame_rate") val frameRate: Double,
    @ColumnInfo(name = "last_played_position") val lastPlayedPosition: Long = 0,
    @ColumnInfo(name = "folder_id") val folderId: Long
)
