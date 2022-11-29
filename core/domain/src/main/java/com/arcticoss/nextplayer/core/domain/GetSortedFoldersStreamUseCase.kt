package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import com.arcticoss.nextplayer.core.data.repository.UiPreferencesRepository
import com.arcticoss.nextplayer.core.domain.models.Folder
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSortedFoldersStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val preferencesRepository: UiPreferencesRepository
) {

    operator fun invoke(): Flow<List<Folder>> {
        return combine(
            mediaRepository.getFolderMediaStream(),
            preferencesRepository.preferencesFlow
        ) { mediaFolders, preferences ->
            val folders = mediaFolders.map { mediaFolder ->
                mediaFolder.copy(
                    mediaList = mediaFolder.mediaList
                        .filter {
                            if (preferences.showHidden) true else !it.title.startsWith(".")
                        }
                )
            }.filter {
                it.mediaList.isNotEmpty()
            }.filter {
                if (preferences.showHidden) true else !it.name.startsWith(".")
            }.map {
                Folder(
                    id = it.id,
                    name = it.name,
                    path = it.path,
                    mediaItemCount = it.mediaList.size
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