package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.FileMediaRepository
import com.arcticoss.nextplayer.core.datastore.datasource.UiPreferencesDataSource
import com.arcticoss.nextplayer.core.model.Folder
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedMediaFolderStreamUseCase @Inject constructor(
    private val fileMediaRepository: FileMediaRepository,
    private val preferencesDataSource: UiPreferencesDataSource
) {

    operator fun invoke(
        folderId: Long
    ): Flow<Folder> {
        return combine(
            fileMediaRepository.getMediaFolderStream(folderId),
            preferencesDataSource.uiPreferencesFlow
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