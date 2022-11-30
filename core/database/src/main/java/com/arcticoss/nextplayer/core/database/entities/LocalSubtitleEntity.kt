package com.arcticoss.nextplayer.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.arcticoss.nextplayer.core.model.LocalSub

@Entity(
    primaryKeys = ["media_id", "path"],
    tableName = "local_subtitle_track",
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
data class LocalSubtitleEntity(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "language") val language: String?,
    @ColumnInfo(name = "selected") val selected: Boolean,
    @ColumnInfo(name = "media_id") val mediaId: Long
)

fun LocalSubtitleEntity.asExternalModel() = LocalSub(
    path = path,
    language = language,
    selected = selected
)
