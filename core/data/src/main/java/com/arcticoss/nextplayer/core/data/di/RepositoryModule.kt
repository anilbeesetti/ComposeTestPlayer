package com.arcticoss.nextplayer.core.data.di

import com.arcticoss.nextplayer.core.data.repository.IMediaRepository
import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindsMediaRepository(
        mediaRepository: MediaRepository
    ): IMediaRepository
}