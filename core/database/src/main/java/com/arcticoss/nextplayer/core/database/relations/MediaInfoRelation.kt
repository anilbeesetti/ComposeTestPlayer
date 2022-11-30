package com.arcticoss.nextplayer.core.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.arcticoss.nextplayer.core.database.entities.AudioTrackEntity
import com.arcticoss.nextplayer.core.database.entities.LocalSubtitleEntity
import com.arcticoss.nextplayer.core.database.entities.MediaEntity
import com.arcticoss.nextplayer.core.database.entities.SubtitleTrackEntity
import com.arcticoss.nextplayer.core.database.entities.ThumbnailEntity
import com.arcticoss.nextplayer.core.database.entities.VideoTrackEntity
import com.arcticoss.nextplayer.core.database.entities.asExternalModel
import com.arcticoss.nextplayer.core.model.Media

data class MediaInfoRelation(
    @Embedded val mediaEntity: MediaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val videoTracks: List<VideoTrackEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val audioTracks: List<AudioTrackEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val subtitleTracks: List<SubtitleTrackEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val localSubtitles: List<LocalSubtitleEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val thumbnail: ThumbnailEntity?
)

fun MediaInfoRelation.asExternalModel() = Media(
    id = mediaEntity.id,
    size = mediaEntity.size,
    width = mediaEntity.width,
    height = mediaEntity.height,
    path = mediaEntity.path,
    title = mediaEntity.title,
    frameRate = mediaEntity.frameRate,
    duration = mediaEntity.duration,
    addedOn = mediaEntity.addedOn,
    lastPlayedOn = mediaEntity.lastPlayedOn,
    lastPlayedPosition = mediaEntity.lastPlayedPosition,
    audioTrackId = mediaEntity.audioTrackId,
    subtitleTrackId = mediaEntity.subtitleTrackId,
    thumbnailPath = thumbnail?.path ?: "",
    localSubtitleTracks = localSubtitles.map { it.asExternalModel() },
    videoTracks = videoTracks.map { it.asExternalModel() },
    audioTracks = audioTracks.map { it.asExternalModel() },
    subtitleTracks = subtitleTracks.map { it.asExternalModel() }
)