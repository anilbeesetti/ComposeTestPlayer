package com.arcticoss.nextplayer.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.nextplayer.core.database.entities.MediaItemEntity
import com.arcticoss.nextplayer.core.database.entities.ThumbnailEntity
import com.arcticoss.nextplayer.core.model.Media

data class MediaItemAndThumbnailRelation(
    @Embedded val mediaEntity: MediaItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_item_id"
    )
    val thumbnail: ThumbnailEntity?
)

fun MediaItemAndThumbnailRelation.asExternalModel() = Media(
    id = mediaEntity.id,
    size = mediaEntity.size,
    width = mediaEntity.width,
    height = mediaEntity.height,
    path = mediaEntity.path,
    title = mediaEntity.title,
    frameRate = mediaEntity.frameRate,
    duration = mediaEntity.duration,
    lastPlayedPosition = mediaEntity.lastPlayedPosition,
    thumbnailPath = thumbnail?.path ?: ""
)
