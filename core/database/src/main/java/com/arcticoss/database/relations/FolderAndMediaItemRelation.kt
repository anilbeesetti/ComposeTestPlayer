package com.arcticoss.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.database.entities.FolderEntity
import com.arcticoss.database.entities.MediaItemEntity
import com.arcticoss.database.entities.asExternalModel
import com.arcticoss.model.MediaFolder

data class FolderAndMediaItemRelation(
    @Embedded val folderEntity: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val mediaItems: List<MediaItemEntity>
)


fun FolderAndMediaItemRelation.asExternalModel() = MediaFolder(
    id = folderEntity.id,
    name = folderEntity.name,
    mediaItems = mediaItems.map { it.asExternalModel() }
)