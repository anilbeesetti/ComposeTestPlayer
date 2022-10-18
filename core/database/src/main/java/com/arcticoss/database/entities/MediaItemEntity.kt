package com.arcticoss.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "media")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "filename") val fileName: String,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "size") val size: Long,
    @ColumnInfo(name = "last_played_position") val lastPlayedPosition: Long = 0,
    @ColumnInfo(name = "folder_id") val folderId: Long
)
