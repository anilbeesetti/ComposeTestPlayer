package com.arcticoss.nextplayer.core.data.repository

import com.arcticoss.nextplayer.core.model.Folder
import com.arcticoss.nextplayer.core.model.Media
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    /**
     * Get a single folder as flow
     */
    fun getMediaFolderStream(id: Long): Flow<Folder>

    /**
     * Get list of folders as flow
     */
    fun getFolderMediaStream(): Flow<List<Folder>>

    /**
     * Get media
     * @param path: path of the media item
     */
    suspend fun getMedia(path: String): Media

    /**
     * Update media item
     */
    suspend fun updateMedia(
        id: Long,
        lastPlayedPosition: Long,
        audioTrackId: String?,
        subtitleTrackId: String?
    )

    /**
     * Sync media
     */
    suspend fun sync()

}