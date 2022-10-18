package com.arcticoss.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.database.entities.FolderEntity
import com.arcticoss.database.entities.MediaItemEntity

data class FolderAndMediaItemRelation(
    @Embedded val folderEntity: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val mediaItems: List<MediaItemEntity>
)
