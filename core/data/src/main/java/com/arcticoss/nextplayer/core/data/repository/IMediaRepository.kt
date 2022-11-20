package com.arcticoss.nextplayer.core.data.repository

import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.Folder
import kotlinx.coroutines.flow.Flow

interface IMediaRepository {

    fun getMediaFolderStream(id: Long): Flow<Folder>

    fun getFolderMediaStream(): Flow<List<Folder>>

    suspend fun syncMedia()

    suspend fun updateMedia(id: Long, lastPlayedPosition: Long)

    suspend fun getMedia(path: String): Media

}