package com.arcticoss.feature.media.domain

import com.arcticoss.data.repository.MediaRepository
import com.arcticoss.model.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaItemStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    operator fun invoke(
        showHidden: Boolean
    ): Flow<List<MediaItem>> {
        return mediaRepository.getFolderMediaStream().map { mediaFolderList ->
            val mediaItemList = mutableListOf<MediaItem>()
            when(showHidden) {
                true -> {
                    mediaFolderList.forEach {
                        mediaItemList.addAll(it.mediaItems)
                    }
                }
                false -> {
                    mediaFolderList.filter { mediaFolder ->
                        mediaFolder.mediaItems.isNotEmpty() && !mediaFolder.name.startsWith(".")
                    }.onEach { mediaFolder ->
                        mediaFolder.mediaItems.filter {
                            !it.title.startsWith(".")
                        }
                    }.forEach {
                        mediaItemList.addAll(it.mediaItems)
                    }
                }
            }
            mediaItemList
        }
    }
}