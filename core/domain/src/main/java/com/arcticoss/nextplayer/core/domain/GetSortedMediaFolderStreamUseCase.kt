package com.arcticoss.nextplayer.core.domain

import com.arcticoss.data.repository.MediaRepository
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.MediaItem
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSortedMediaFolderStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    fun getMedia(
        folderId: Long,
        showHidden: Boolean,
        sortBy: SortBy,
        sortOrder: SortOrder
    ): Flow<MediaFolder> {
        return mediaRepository.getMediaFolderStream(folderId).map { mediaFolder ->
            mediaFolder.also { folder ->
                folder.mediaItems.filter {
                    if (showHidden) true else !it.title.startsWith(".")
                }
            }.also { folder ->
                when (sortOrder) {
                    SortOrder.Ascending -> {
                        when (sortBy) {
                            SortBy.Title -> folder.mediaItems.sortedBy { it.title.lowercase() }
                            SortBy.Length -> folder.mediaItems.sortedBy { it.duration }
                        }
                    }
                    SortOrder.Descending -> {
                        when (sortBy) {
                            SortBy.Title -> folder.mediaItems.sortedByDescending { it.title.lowercase() }
                            SortBy.Length -> folder.mediaItems.sortedByDescending { it.duration }
                        }
                    }
                }
            }
        }
    }
}