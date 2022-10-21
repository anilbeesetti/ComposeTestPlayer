package com.arcticoss.nextplayer.core.domain

import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.MediaItem
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaItemStreamUseCase @Inject constructor(
    private val mediaRepository: IMediaRepository
) {

    operator fun invoke(
        showHidden: Boolean,
        sortBy: SortBy,
        sortOrder: SortOrder
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
            when(sortOrder) {
                SortOrder.Ascending -> {
                    when(sortBy) {
                        SortBy.Title -> mediaItemList.sortedBy { it.title.lowercase() }
                        SortBy.Length -> mediaItemList.sortedBy { it.duration }
                    }
                }
                SortOrder.Descending -> {
                    when(sortBy) {
                        SortBy.Title -> mediaItemList.sortedByDescending { it.title.lowercase() }
                        SortBy.Length -> mediaItemList.sortedByDescending { it.duration }
                    }
                }
            }
        }
    }
}