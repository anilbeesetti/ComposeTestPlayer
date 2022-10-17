package com.arcticoss.data.repository

import android.os.Environment
import com.arcticoss.data.utils.getVideos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class MediaRepository @Inject constructor(): IMediaRepository {

    private val externalDir = Environment.getExternalStorageDirectory()

    override fun getAllMedia(): Flow<List<MediaItem>> =
        externalDir.getVideos().map {
            it.map { file ->
                MediaItem(file)
            }
        }

}

data class MediaItem(
    val file: File
)