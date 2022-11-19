package com.arcticoss.nextplayer.core.database.entities

import androidx.room.*

/**
* Defines thumbnail for [MediaItemEntity]
* It has one to one relationship with [MediaItemEntity]
*/
@Entity(
    tableName = "thumbnail",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_item_id"),
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["media_item_id"])]
)
data class ThumbnailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "media_item_id") val mediaItemId: Long?
)
