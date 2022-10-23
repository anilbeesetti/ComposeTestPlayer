package com.arcticoss.data.repository

import com.arcticoss.model.MediaFolder
import kotlinx.coroutines.flow.Flow

interface IMediaRepository {

    fun getMediaFolderStream(id: Long): Flow<MediaFolder>

    fun getFolderMediaStream(): Flow<List<MediaFolder>>

    suspend fun syncMedia()

}