package com.arcticoss.nextplayer.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.arcticoss.nextplayer.core.data.utils.asAudioTrackEntity
import com.arcticoss.nextplayer.core.data.utils.asMediaItemEntity
import com.arcticoss.nextplayer.core.data.utils.asSubtitleTrackEntity
import com.arcticoss.nextplayer.core.data.utils.asVideoTrackEntity
import com.arcticoss.nextplayer.core.data.utils.getFoldersAndVideos
import com.arcticoss.nextplayer.core.data.utils.notExists
import com.arcticoss.nextplayer.core.data.utils.saveThumbnail
import com.arcticoss.nextplayer.core.database.daos.AudioTrackDao
import com.arcticoss.nextplayer.core.database.daos.FolderDao
import com.arcticoss.nextplayer.core.database.daos.LocalSubtitleDao
import com.arcticoss.nextplayer.core.database.daos.MediaDao
import com.arcticoss.nextplayer.core.database.daos.SubtitleTrackDao
import com.arcticoss.nextplayer.core.database.daos.ThumbnailDao
import com.arcticoss.nextplayer.core.database.daos.VideoTrackDao
import com.arcticoss.nextplayer.core.database.entities.FolderEntity
import com.arcticoss.nextplayer.core.database.entities.LocalSubtitleEntity
import com.arcticoss.nextplayer.core.database.entities.ThumbnailEntity
import com.arcticoss.nextplayer.core.database.relations.FolderAndMediaRelation
import com.arcticoss.nextplayer.core.database.relations.asExternalModel
import com.arcticoss.nextplayer.core.model.Folder
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.mediainfo.FrameLoader
import com.arcticoss.nextplayer.mediainfo.MediaInfoBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FileMediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
    private val folderDao: FolderDao,
    private val videoTrackDao: VideoTrackDao,
    private val audioTrackDao: AudioTrackDao,
    private val subtitleTrackDao: SubtitleTrackDao,
    private val thumbnailDao: ThumbnailDao,
    private val localSubtitleDao: LocalSubtitleDao,
    @ApplicationContext private val context: Context
) : MediaRepository {

    private val storageDir = Environment.getExternalStorageDirectory()
    private val dataDir = context.getExternalFilesDir(null)

    override fun getMediaFolderStream(id: Long): Flow<Folder> = folderDao
        .getFolderAndMediaItemStream(id)
        .map { it.asExternalModel() }


    override fun getFolderMediaStream(): Flow<List<Folder>> = folderDao
        .getFolderAndMediaItemStream()
        .map { it.map(FolderAndMediaRelation::asExternalModel) }


    override suspend fun getMedia(path: String): Media {
        return mediaDao.get(path).asExternalModel()
    }

    override suspend fun updateMedia(
        id: Long,
        lastPlayedPosition: Long,
        audioTrackId: String?,
        subtitleTrackId: String?,
        playedOn: Long?
    ) {
        var mediaEntity = mediaDao.get(id)
        mediaEntity = mediaEntity.copy(
            lastPlayedPosition = lastPlayedPosition,
            lastPlayedOn = playedOn
        )
        if (audioTrackId != null) {
            mediaEntity = mediaEntity.copy(audioTrackId = audioTrackId)
        }
        if (subtitleTrackId != null) {
            mediaEntity = mediaEntity.copy(subtitleTrackId = subtitleTrackId)
        }
        mediaDao.update(mediaEntity)
    }

    override suspend fun sync() = withContext(Dispatchers.IO) {
        // cleanup
        deleteUnavailableFolderEntities()
        deleteUnavailableMediaEntities()
        deleteUnusedThumbnailEntities()
        deleteUnavailableLocalSubtitles()

        // sync for new media
        syncFoldersAndVideos()
        syncLocalSubtitles()
        syncThumbnails()
    }

    private suspend fun syncFoldersAndVideos() {
        withContext(Dispatchers.IO) {
            launch {
                storageDir.getFoldersAndVideos().collect { file ->
                    if (file.isDirectory) {
                        syncFolder(file)
                    } else {
                        launch { syncVideoFile(file) }
                    }
                }
            }.join()
        }
    }

    private suspend fun syncFolder(folder: File) {
        if (!folderDao.isExist(folder.path)) {
            val folderEntity = FolderEntity(
                name = if (folder.name == "0") "Internal Storage" else folder.name,
                path = folder.path
            )
            folderDao.insert(folderEntity)
        }
    }

    private suspend fun syncVideoFile(videoFile: File) {
        if (!mediaDao.isExist(videoFile.path)) {
            // If media does not exist in add media to database
            syncMediaItem(videoFile = videoFile)
        } else {
            // Sometimes like while downloading a file it will not be synced properly
            // this ensures it will synced properly by checking file size
            val mediaItem = mediaDao.get(videoFile.path)
            if (mediaItem.mediaEntity.size != videoFile.length()) {
                syncMediaItem(mediaId = mediaItem.mediaEntity.id, videoFile = videoFile)
            }
        }
    }

    private suspend fun syncMediaItem(mediaId: Long = 0, videoFile: File) {
        val mediaInfoBuilder = MediaInfoBuilder()
        val mediaInfo = mediaInfoBuilder.from(videoFile).build()

        // Syncing media item
        val mediaItemId = mediaDao.insert(
            mediaInfo.asMediaItemEntity(
                id = mediaId,
                folderId = folderDao.id(videoFile.parentFile!!.path)
            )
        )

        // Syncing video streams
        mediaInfo.videoStreams.forEach {
            videoTrackDao.insert(it.asVideoTrackEntity(mediaItemId))
        }

        // Syncing audio streams
        mediaInfo.audioStreams.forEach {
            audioTrackDao.insert(it.asAudioTrackEntity(mediaItemId))
        }

        // Syncing subtitle streams
        mediaInfo.subtitleStreams.forEach {
            subtitleTrackDao.insert(it.asSubtitleTrackEntity(mediaItemId))
        }
    }

    private suspend fun syncThumbnails() {
        val frameLoader = FrameLoader()
        mediaDao.getMediaEntities().forEach {
            if (!thumbnailDao.isExist(it.id) && it.width > 0 && it.height > 0) {
                val bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
                val result = frameLoader.loadFrame(it.path, bitmap)
                if (result) {
                    dataDir?.let { dir ->
                        val thumbnailPath = bitmap.saveThumbnail(dir.path, 50)
                        thumbnailDao.insert(
                            ThumbnailEntity(path = thumbnailPath, mediaId = it.id)
                        )
                    }
                }
            }
        }
    }

    private suspend fun syncLocalSubtitles() {
        mediaDao.getMediaEntities().forEach { mediaEntity ->
            val videoFile = File(mediaEntity.path)
            videoFile.parentFile?.listFiles()?.forEach {
                if (it.name.contains(videoFile.nameWithoutExtension) && it.extension == "srt") {
                    if (!localSubtitleDao.isExist(it.path)) {
                        localSubtitleDao.insert(
                            LocalSubtitleEntity(
                                path = it.path,
                                language = null,
                                selected = false,
                                mediaId = mediaEntity.id
                            )
                        )
                    }
                }
            }
        }
    }


    // clean up function to delete unavailable directories in storage
    private suspend fun deleteUnavailableFolderEntities() {
        folderDao.getFolderEntities().forEach { folderEntity ->
            if (File(folderEntity.path).notExists()) {
                folderDao.delete(folderEntity)
            }
        }
    }

    // clean up function to delete unavailable video files in storage
    private suspend fun deleteUnavailableMediaEntities() {
        mediaDao.getMediaEntities().forEach { mediaEntity ->
            if (File(mediaEntity.path).notExists()) {
                mediaDao.delete(mediaEntity)
            }
        }
    }

    // clean up function to delete thumbnails of deleted media files
    private suspend fun deleteUnusedThumbnailEntities() {
        thumbnailDao.getThumbnailEntities().forEach { thumbnailEntity ->
            if (thumbnailEntity.mediaId == null) {
                File(thumbnailEntity.path).delete()
            }
        }
    }

    // clean up function to delete unavailable local subtitles
    private suspend fun deleteUnavailableLocalSubtitles() {
        localSubtitleDao.getLocalSubtitleEntities().forEach { localSubtitleEntity ->
            if (File(localSubtitleEntity.path).notExists()) {
                localSubtitleDao.delete(localSubtitleEntity)
            }
        }
    }
}