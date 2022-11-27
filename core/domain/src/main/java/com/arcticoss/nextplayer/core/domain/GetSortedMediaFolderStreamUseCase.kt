package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.model.Folder
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedMediaFolderStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesDataSource: InterfacePreferencesDataSource
) {

    operator fun invoke(
        folderId: Long
    ): Flow<Folder> {
        return combine(
            mediaRepository.getMediaFolderStream(folderId),
            preferencesDataSource.preferencesFlow
        ) { mediaFolder, preferences ->

            val media = mediaFolder.copy(
                mediaList = mediaFolder.mediaList.filter {
                    if (preferences.showHidden) true else !it.title.startsWith(".")
                }
            )
            media.copy(mediaList = when (preferences.sortOrder) {
                SortOrder.Ascending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> media.mediaList.sortedBy { it.title.lowercase() }
                        SortBy.Length -> media.mediaList.sortedBy { it.duration }
                    }
                }
                SortOrder.Descending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> media.mediaList.sortedByDescending { it.title.lowercase() }
                        SortBy.Length -> media.mediaList.sortedByDescending { it.duration }
                    }
                }
            })
        }
    }
}