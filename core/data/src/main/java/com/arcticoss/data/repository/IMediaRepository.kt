package com.arcticoss.data.repository

import kotlinx.coroutines.flow.Flow

interface IMediaRepository {

    fun getAllMedia(): Flow<List<MediaItem>>
}