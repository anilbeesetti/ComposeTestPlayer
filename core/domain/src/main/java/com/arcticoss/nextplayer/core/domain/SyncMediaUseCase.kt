package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.IMediaRepository
import javax.inject.Inject

class SyncMediaUseCase @Inject constructor(
    private val mediaRepository: IMediaRepository
) {

    suspend operator fun invoke() {
        mediaRepository.syncMedia()
    }
}