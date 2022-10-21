package com.arcticoss.nextplayer.feature.media.domain

import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaFolderStreamUseCase @Inject constructor(
    private val mediaRepository: IMediaRepository
) {

    operator fun invoke(
        showHidden: Boolean,
        sortBy: SortBy,
        sortOrder: SortOrder
    ): Flow<List<MediaFolder>> {
        return mediaRepository.getFolderMediaStream().map { mediaFolderList ->
            val mediaFolders = when (showHidden) {
                true -> {
                    mediaFolderList.filter {
                        it.mediaItems.isNotEmpty()
                    }
                }
                false -> {
                    mediaFolderList.filter { mediaFolder ->
                        mediaFolder.mediaItems.isNotEmpty() && !mediaFolder.name.startsWith(".")
                    }.onEach {
                        it.mediaItems.filter { mediaItem ->
                            !mediaItem.title.startsWith(".")
                        }
                    }
                }
            }
            when (sortOrder) {
                SortOrder.Ascending -> {
                    when (sortBy) {
                        SortBy.Title -> mediaFolders.sortedBy { it.name.lowercase() }
                            .onEach { mediaFolder ->
                                mediaFolder.mediaItems.sortedBy { it.title.lowercase() }
                            }
                        SortBy.Length -> mediaFolders.sortedBy { it.name.lowercase() }
                            .onEach { mediaFolder ->
                                mediaFolder.mediaItems.sortedBy { it.duration }
                            }
                    }
                }
                SortOrder.Descending -> {
                    when (sortBy) {
                        SortBy.Title -> mediaFolders.sortedByDescending { it.name.lowercase() }
                            .onEach { mediaFolder ->
                                mediaFolder.mediaItems.sortedByDescending { it.title.lowercase() }
                            }
                        SortBy.Length -> mediaFolders.sortedByDescending { it.name.lowercase() }
                            .onEach { mediaFolder ->
                                mediaFolder.mediaItems.sortedByDescending { it.duration }
                            }
                    }
                }
            }
        }
    }
}