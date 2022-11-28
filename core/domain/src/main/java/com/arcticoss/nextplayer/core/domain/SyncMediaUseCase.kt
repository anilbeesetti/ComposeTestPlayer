package com.arcticoss.nextplayer.core.domain

import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import javax.inject.Inject

class SyncMediaUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {

    suspend operator fun invoke() {
        mediaRepository.sync()
    }
}