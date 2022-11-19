package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import com.arcticoss.model.MediaFolder
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedMediaFolderStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesDataSource: InterfacePreferencesDataSource
) {

    operator fun invoke(
        folderId: Long
    ): Flow<MediaFolder> {
        return combine(
            mediaRepository.getMediaFolderStream(folderId),
            preferencesDataSource.preferencesFlow
        ) { mediaFolder, preferences ->

            val media = mediaFolder.copy(
                mediaItems = mediaFolder.mediaItems.filter {
                    if (preferences.showHidden) true else !it.title.startsWith(".")
                }
            )
            media.copy(mediaItems = when (preferences.sortOrder) {
                SortOrder.Ascending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> media.mediaItems.sortedBy { it.title.lowercase() }
                        SortBy.Length -> media.mediaItems.sortedBy { it.duration }
                    }
                }
                SortOrder.Descending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> media.mediaItems.sortedByDescending { it.title.lowercase() }
                        SortBy.Length -> media.mediaItems.sortedByDescending { it.duration }
                    }
                }
            })
        }
    }
}