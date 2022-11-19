package com.arcticoss.nextplayer.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.nextplayer.core.database.entities.AudioTrackEntity
import com.arcticoss.nextplayer.core.database.entities.MediaItemEntity
import com.arcticoss.nextplayer.core.database.entities.SubtitleTrackEntity
import com.arcticoss.nextplayer.core.database.entities.VideoTrackEntity

data class MediaInfoRelation(
    @Embedded val mediaEntity: MediaItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_item_id"
    )
    val videoTracks: List<VideoTrackEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_item_id"
    )
    val audioTracks: List<AudioTrackEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_item_id"
    )
    val subtitleTracks: List<SubtitleTrackEntity>
)
