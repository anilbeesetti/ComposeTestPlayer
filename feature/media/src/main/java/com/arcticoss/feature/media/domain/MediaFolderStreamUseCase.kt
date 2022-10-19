package com.arcticoss.feature.media.domain

import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.model.MediaFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaFolderStreamUseCase @Inject constructor(
    private val mediaRepository: IMediaRepository
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