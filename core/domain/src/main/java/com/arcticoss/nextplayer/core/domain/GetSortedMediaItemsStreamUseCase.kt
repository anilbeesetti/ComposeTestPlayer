package com.arcticoss.nextplayer.core.domain

import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.MediaItem
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedMediaItemsStreamUseCase @Inject constructor(
    private val mediaRepository: IMediaRepository,
    private val preferencesDataSource: InterfacePreferencesDataSource
) {

    operator fun invoke(): Flow<List<MediaItem>> {
        return combine(
            mediaRepository.getFolderMediaStream(),
            preferencesDataSource.preferencesFlow
        ) { mediaFolders, preferences ->
            val folders = mediaFolders.filter {
                it.mediaItems.isNotEmpty()
            }.filter {
                if (preferences.showHidden) true else !it.name.startsWith(".")
            }

            val mediaItemList = mutableListOf<MediaItem>()

            folders.forEach {
                mediaItemList.addAll(it.mediaItems)
            }

            val mediaItems = mediaItemList.filter {
                if (preferences.showHidden) true else !it.title.startsWith(".")
            }


            when(preferences.sortOrder) {
                SortOrder.Ascending -> {
                    when(preferences.sortBy) {
                        SortBy.Title -> mediaItems.sortedBy { it.title.lowercase() }
                        SortBy.Length -> mediaItems.sortedBy { it.duration }
                    }
                }
                SortOrder.Descending -> {
                    when(preferences.sortBy) {
                        SortBy.Title -> mediaItems.sortedByDescending { it.title.lowercase() }
                        SortBy.Length -> mediaItems.sortedByDescending { it.duration }
                    }
                }
            }
        }
    }
}