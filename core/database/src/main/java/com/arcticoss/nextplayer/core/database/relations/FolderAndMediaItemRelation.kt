package com.arcticoss.nextplayer.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.nextplayer.core.database.entities.FolderEntity
import com.arcticoss.nextplayer.core.database.entities.MediaEntity
import com.arcticoss.nextplayer.core.model.Folder

data class FolderAndMediaItemRelation(
    @Embedded val folderEntity: FolderEntity,
    @Relation(
        entity = MediaEntity::class,
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val mediaItems: List<MediaItemAndThumbnailRelation>
)


fun FolderAndMediaItemRelation.asExternalModel() = Folder(
    id = folderEntity.id,
    name = folderEntity.name,
    path = folderEntity.path,
    mediaList = mediaItems.map { it.asExternalModel() }
)