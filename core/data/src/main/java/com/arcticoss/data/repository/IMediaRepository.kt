package com.arcticoss.data.repository

import com.arcticoss.model.MediaFolder
import com.arcticoss.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface IMediaRepository {

    fun getMediaStream(): Flow<List<MediaItem>>

    fun getFolderMediaStream(): Flow<List<MediaFolder>>

    suspend fun syncMedia()

}