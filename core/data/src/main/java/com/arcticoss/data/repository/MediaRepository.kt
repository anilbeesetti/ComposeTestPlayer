package com.arcticoss.data.repository

import android.os.Environment
import com.arcticoss.data.utils.getFolders
import com.arcticoss.data.utils.getVideos
import com.arcticoss.data.utils.notExists
import com.arcticoss.database.daos.*
import com.arcticoss.database.entities.FolderEntity
import com.arcticoss.database.entities.MediaItemEntity
import com.arcticoss.database.entities.asExternalModel
import com.arcticoss.database.relations.FolderAndMediaItemRelation
import com.arcticoss.database.relations.asExternalModel
import com.arcticoss.mediainfo.MediaInfoBuilder
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val folderDao: FolderDao,
    private val videoTrackDao: VideoTrackDao,
    private val audioTrackDao: AudioTrackDao,
    private val subtitleTrackDao: SubtitleTrackDao
): IMediaRepository {

    private val externalDir = Environment.getExternalStorageDirectory()

    override fun getMediaStream(): Flow<List<MediaItem>> =
        mediaItemDao.getMediaItemEntitiesStream()
            .map { it.map(MediaItemEntity::asExternalModel) }


    override fun getFolderMediaStream(): Flow<List<MediaFolder>> =
        folderDao.getFolderAndMediaItemStream()
            .map { it.map(FolderAndMediaItemRelation::asExternalModel) }

    override suspend fun syncMedia() = withContext(Dispatchers.IO) {
        syncDatabase()
        syncFolders()
        syncVideos()
    }

    private suspend fun syncFolders() {
        externalDir.getFolders().forEach {
            if (!folderDao.isExist(it.path)) {
                folderDao.insert(
                    FolderEntity(
                        name = it.name,
                        path = it.path
                    )
                )
            }
        }
    }

    private suspend fun syncVideos() {
        val mediaInfoBuilder = MediaInfoBuilder()
        externalDir.getVideos().collect {
            if (!mediaItemDao.isExist(it.path)) {
                val mediaInfo = mediaInfoBuilder.from(it).build()
                mediaItemDao.insert(
                    MediaItemEntity(
                        title = mediaInfo.title,
                        size = mediaInfo.size,
                        path = mediaInfo.filePath,
                        width = mediaInfo.width,
                        duration = mediaInfo.duration,
                        height = mediaInfo.height,
                        frameRate = mediaInfo.frameRate,
                        folderId = folderDao.id(it.parentFile!!.path)
                    )
                )
            }
        }
    }

    private suspend fun syncDatabase() {
        mediaItemDao.getMediaItemEntities().forEach { mediaItemEntity ->
            if (File(mediaItemEntity.path).notExists()) {
                mediaItemDao.delete(mediaItemEntity)
            }
        }
    }
}