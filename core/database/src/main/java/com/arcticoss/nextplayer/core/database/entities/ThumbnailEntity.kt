package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Defines thumbnail for [MediaEntity]
 * It has one to one relationship with [MediaEntity]
 */
@Entity(
    tableName = "thumbnail",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_id"),
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["media_id"])]
)
data class ThumbnailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "media_id") val mediaId: Long?
)
