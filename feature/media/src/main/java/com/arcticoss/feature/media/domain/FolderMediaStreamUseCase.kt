package com.arcticoss.feature.media.domain

import com.arcticoss.data.repository.MediaRepository
import com.arcticoss.model.MediaFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FolderMediaStreamUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    operator fun invoke(
        showHidden: Boolean
    ): Flow<List<MediaFolder>> {
        return mediaRepository.getFolderMediaStream().map { mediaFolderList ->
            when(showHidden) {
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
        }
    }
}