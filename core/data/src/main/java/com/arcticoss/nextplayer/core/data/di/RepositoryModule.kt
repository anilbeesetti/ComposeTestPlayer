package com.arcticoss.nextplayer.core.data.di

import com.arcticoss.nextplayer.core.data.repository.MediaRepository
import com.arcticoss.nextplayer.core.data.repository.FileMediaRepository
import com.arcticoss.nextplayer.core.data.repository.UiPreferencesRepository
import com.arcticoss.nextplayer.core.data.repository.UserUiPreferencesRepository
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
        mediaRepository: FileMediaRepository
    ): MediaRepository


    @Binds
    fun bindsUiPreferencesRepository(
        uiPreferencesRepository: UserUiPreferencesRepository
    ): UiPreferencesRepository
}