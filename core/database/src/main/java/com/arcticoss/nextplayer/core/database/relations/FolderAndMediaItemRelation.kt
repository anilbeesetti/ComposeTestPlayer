package com.arcticoss.nextplayer.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.nextplayer.core.database.entities.FolderEntity
import com.arcticoss.nextplayer.core.database.entities.MediaItemEntity
import com.arcticoss.model.MediaFolder

data class FolderAndMediaItemRelation(
    @Embedded val folderEntity: FolderEntity,
    @Relation(
        entity = MediaItemEntity::class,
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val mediaItems: List<MediaItemAndThumbnailRelation>
)


fun FolderAndMediaItemRelation.asExternalModel() = MediaFolder(
    id = folderEntity.id,
    name = folderEntity.name,
    path = folderEntity.path,
    mediaItems = mediaItems.map { it.asExternalModel() }
)