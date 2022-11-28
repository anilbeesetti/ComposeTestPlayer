package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import com.arcticoss.nextplayer.core.data.repository.UiPreferencesRepository
import com.arcticoss.nextplayer.core.model.Media
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedMediaItemsStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: UiPreferencesRepository
) {

    operator fun invoke(): Flow<List<Media>> {
        return combine(
            mediaRepository.getFolderMediaStream(),
            preferencesRepository.preferencesFlow
        ) { mediaFolders, preferences ->
            val folders = mediaFolders.filter {
                it.mediaList.isNotEmpty()
            }.filter {
                if (preferences.showHidden) true else !it.name.startsWith(".")
            }

            val mediaItemList = mutableListOf<Media>()

            folders.forEach {
                mediaItemList.addAll(it.mediaList)
            }

            val mediaItems = mediaItemList.filter {
                if (preferences.showHidden) true else !it.title.startsWith(".")
            }


            when (preferences.sortOrder) {
                SortOrder.Ascending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> mediaItems.sortedBy { it.title.lowercase() }
                        SortBy.Length -> mediaItems.sortedBy { it.duration }
                    }
                }
                SortOrder.Descending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> mediaItems.sortedByDescending { it.title.lowercase() }
                        SortBy.Length -> mediaItems.sortedByDescending { it.duration }
                    }
                }
            }
        }
    }
}