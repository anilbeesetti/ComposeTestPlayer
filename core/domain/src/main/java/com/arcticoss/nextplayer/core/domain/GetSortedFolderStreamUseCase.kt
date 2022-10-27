package com.arcticoss.nextplayer.core.domain

import com.arcticoss.data.repository.MediaRepository
import com.arcticoss.model.SortBy
import com.arcticoss.model.SortOrder
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.domain.models.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedFoldersStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesDataSource: InterfacePreferencesDataSource
) {

    operator fun invoke(): Flow<List<Folder>> {
        return combine(
            mediaRepository.getFolderMediaStream(),
            preferencesDataSource.preferencesFlow
        ) { mediaFolders, preferences ->
            val folders = mediaFolders.map { mediaFolder ->
                mediaFolder.copy(
                    mediaItems = mediaFolder.mediaItems
                        .filter {
                            if (preferences.showHidden) true else !it.title.startsWith(".")
                        }
                )
            }.filter {
                it.mediaItems.isNotEmpty()
            }.filter {
                if (preferences.showHidden) true else !it.name.startsWith(".")
            }.map {
                Folder(
                    id = it.id,
                    name = it.name,
                    path = it.path,
                    mediaItemCount = it.mediaItems.size
                )
            }

            when (preferences.sortOrder) {
                SortOrder.Ascending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> folders.sortedBy { it.name.lowercase() }
                        SortBy.Length -> folders.sortedBy { it.name.lowercase() }
                    }
                }
                SortOrder.Descending -> {
                    when (preferences.sortBy) {
                        SortBy.Title -> folders.sortedByDescending { it.name.lowercase() }
                        SortBy.Length -> folders.sortedByDescending { it.name.lowercase() }
                    }
                }
            }
        }
    }
}