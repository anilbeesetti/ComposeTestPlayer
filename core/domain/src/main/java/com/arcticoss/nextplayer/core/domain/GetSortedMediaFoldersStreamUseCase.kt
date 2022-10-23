package com.arcticoss.nextplayer.core.domain


import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSortedMediaFoldersStreamUseCase @Inject constructor(
    private val mediaRepository: IMediaRepository
) {

    fun getAllMedia(
        showHidden: Boolean,
        sortBy: SortBy,
        sortOrder: SortOrder
    ): Flow<List<MediaFolder>> {
        return mediaRepository.getFolderMediaStream().map { mediaFolderList ->
            mediaFolderList.map { mediaFolder ->
                 mediaFolder.copy(
                     mediaItems = mediaFolder.mediaItems.filter { if (showHidden) true else !it.title.startsWith(".") }
                 )
            }.filter {
                it.mediaItems.isNotEmpty()
            }.filter {
                if (showHidden) true else !it.name.startsWith(".")
            }.also { mediaFolders ->
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
}