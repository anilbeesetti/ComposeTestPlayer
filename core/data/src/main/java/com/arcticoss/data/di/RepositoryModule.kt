package com.arcticoss.data.di

import com.arcticoss.data.repository.IMediaRepository
import com.arcticoss.data.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindsMediaRepository(
        mediaRepository: MediaRepository
    ): IMediaRepository
}