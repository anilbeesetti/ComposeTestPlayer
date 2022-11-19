package com.arcticoss.nextplayer.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.arcticoss.nextplayer.mediainfo.FrameLoader
import com.arcticoss.nextplayer.mediainfo.MediaInfoBuilder
import com.arcticoss.model.Media
import com.arcticoss.model.MediaFolder
import com.arcticoss.nextplayer.core.data.utils.*
import com.arcticoss.nextplayer.core.database.daos.*
import com.arcticoss.nextplayer.core.database.entities.FolderEntity
import com.arcticoss.nextplayer.core.database.entities.ThumbnailEntity
import com.arcticoss.nextplayer.core.database.relations.FolderAndMediaItemRelation
import com.arcticoss.nextplayer.core.database.relations.asExternalModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val folderDao: FolderDao,
    private val videoTrackDao: VideoTrackDao,
    private val audioTrackDao: AudioTrackDao,
    private val subtitleTrackDao: SubtitleTrackDao,
    private val thumbnailDao: ThumbnailDao,
    @ApplicationContext private val context: Context
): IMediaRepository {

    private val storageDir = Environment.getExternalStorageDirectory()
    private val dataDir = context.getExternalFilesDir(null)

    override fun getMediaFolderStream(id: Long): Flow<MediaFolder> =
        folderDao.getFolderAndMediaItemStream(id)
            .map { it.asExternalModel() }


    override fun getFolderMediaStream(): Flow<List<MediaFolder>> =
        folderDao.getFolderAndMediaItemStream()
            .map { it.map(FolderAndMediaItemRelation::asExternalModel) }

    override suspend fun syncMedia() = withContext(Dispatchers.IO) {
        syncDatabase()
        syncFoldersAndVideos()
        syncThumbnails()
    }

    override suspend fun updateMedia(id: Long, lastPlayedPosition: Long) {
        val mediaEntity = mediaItemDao.get(id)
        mediaItemDao.update(
            mediaEntity.copy(lastPlayedPosition = lastPlayedPosition)
        )
    }

    override suspend fun getMedia(path: String): Media {
        return mediaItemDao.get(path).asExternalModel()
    }

    private suspend fun syncFoldersAndVideos() {
        val job = CoroutineScope(Dispatchers.IO).launch {
            storageDir.getFoldersAndVideos().collect { file ->
                if (file.isDirectory) {
                    syncFolder(file)
                } else {
                    launch { syncVideoFile(file) }
                }
            }
        }
        job.join()
    }

    private suspend fun syncFolder(folder: File) {
        if (!folderDao.isExist(folder.path)) {
            folderDao.insert(
                FolderEntity(
                    name = folder.name,
                    path = folder.path
                )
            )
        }
    }

    private suspend fun syncVideoFile(videoFile: File) {
        if (!mediaItemDao.isExist(videoFile.path)) {
            val mediaInfoBuilder = MediaInfoBuilder()
            val mediaInfo = mediaInfoBuilder.from(videoFile).build()

            val mediaItemId = mediaItemDao.insert(
                mediaInfo.asMediaItemEntity(folderDao.id(videoFile.parentFile!!.path))
            )
            mediaInfo.videoStreams.forEach {
                videoTrackDao.insert(it.asVideoTrackEntity(mediaItemId))
            }
            mediaInfo.audioStreams.forEach {
                audioTrackDao.insert(it.asAudioTrackEntity(mediaItemId))
            }
            mediaInfo.subtitleStreams.forEach {
                subtitleTrackDao.insert(it.asSubtitleTrackEntity(mediaItemId))
            }
        }
    }

    private suspend fun syncThumbnails() {
        val frameLoader = FrameLoader()
        mediaItemDao.getMediaItemEntities().forEach {
            if (!thumbnailDao.isExist(it.id) && it.width > 0 && it.height > 0) {
                val bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                val result = frameLoader.loadFrame(it.path, bitmap)
                if (result) {
                    dataDir?.let { dir ->
                        val thumbnailPath = bitmap.saveThumbnail(dir.path, 50)
                        thumbnailDao.insert(
                            ThumbnailEntity(
                                path = thumbnailPath,
                                mediaItemId = it.id
                            )
                        )
                    }

                }
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