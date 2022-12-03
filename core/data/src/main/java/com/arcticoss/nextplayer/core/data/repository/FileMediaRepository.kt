package com.arcticoss.nextplayer.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.arcticoss.nextplayer.core.data.utils.asAudioTrackEntity
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
import com.arcticoss.nextplayer.core.database.entities.MediaEntity
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
        return mediaDao.getWithInfo(path).asExternalModel()
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
        syncMediaInfo()
        syncLocalSubtitles()
        syncThumbnails()
    }

    private suspend fun syncFoldersAndVideos() = withContext(Dispatchers.IO) {
        storageDir.getFoldersAndVideos().collect { file ->
            if (file.isDirectory) syncFolder(file) else launch { syncVideo(file) }
        }
    }

    private suspend fun syncFolder(folder: File) {
        if (!folderDao.isExist(folder.path)) {
            val folderEntity = FolderEntity(
                name = if (folder.path == "/emulated/storage/0") "Internal Storage" else folder.name,
                path = folder.path
            )
            folderDao.insert(folderEntity)
        }
    }

    private suspend fun syncVideo(videoFile: File) {
        if (!mediaDao.isExist(videoFile.path)) {

            // If media does not exist in add media to database
            val mediaEntity = MediaEntity(
                title = videoFile.name,
                path = videoFile.path,
                size = videoFile.length(),
                folderId = folderDao.id(videoFile.parentFile!!.path)
            )
            mediaDao.insert(mediaEntity)
        } else {
            // Sometimes like while downloading a file it will not be synced properly
            // this ensures it will synced properly by checking file size
            val mediaEntity = mediaDao.get(videoFile.path)
            if (mediaEntity.size != videoFile.length()) {
                syncMediaItem(mediaEntity)
            }
        }
    }

    private suspend fun syncMediaItem(mediaEntity: MediaEntity) {
        val videoFile = File(mediaEntity.path)
        val mediaInfoBuilder = MediaInfoBuilder()
        val mediaInfo = mediaInfoBuilder.from(videoFile).build()

        // Syncing media item
        mediaDao.update(
            mediaEntity.copy(
                width = mediaInfo.width,
                duration = mediaInfo.duration,
                height = mediaInfo.height,
                frameRate = mediaInfo.frameRate,
                addedOn = mediaInfo.lastModified,
            )
        )

        // Syncing video streams
        mediaInfo.videoStreams.forEach {
            videoTrackDao.insert(it.asVideoTrackEntity(mediaEntity.id))
        }

        // Syncing audio streams
        mediaInfo.audioStreams.forEach {
            audioTrackDao.insert(it.asAudioTrackEntity(mediaEntity.id))
        }

        // Syncing subtitle streams
        mediaInfo.subtitleStreams.forEach {
            subtitleTrackDao.insert(it.asSubtitleTrackEntity(mediaEntity.id))
        }
    }

    private suspend fun syncMediaInfo() = withContext(Dispatchers.IO) {
        mediaDao.getMediaEntities().forEach { mediaEntity ->
            if (mediaEntity.duration == null) {
                launch {
                    syncMediaItem(mediaEntity)
                }
            }
        }
    }


    private suspend fun syncThumbnails() {
        val frameLoader = FrameLoader()
        mediaDao.getMediaEntities().forEach {
            val heightAndWidthNotNull = it.width != 0 && it.height != 0

            if (!thumbnailDao.isExist(it.id) && heightAndWidthNotNull && it.width!! > 0 && it.height!! > 0) {

                // create a bitmap to hold frame
                val bitmap = Bitmap.createBitmap(it.width!!, it.height!!, Bitmap.Config.ARGB_8888)

                // load frame into bitmap
                val isFrameLoadedIntoBitmap = frameLoader.loadFrame(it.path, bitmap)

                if (isFrameLoadedIntoBitmap && dataDir != null) {

                    // save and get bitmap path
                    val thumbnailPath = bitmap.saveThumbnail(dataDir.path, 50)

                    // insert thumbnail entity into database
                    val thumbnailEntity = ThumbnailEntity(path = thumbnailPath, mediaId = it.id)
                    thumbnailDao.insert(thumbnailEntity)
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