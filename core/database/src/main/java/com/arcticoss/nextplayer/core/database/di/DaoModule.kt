package com.arcticoss.nextplayer.core.database.di

import com.arcticoss.nextplayer.core.database.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideFolderDao(db: MediaDatabase) = db.folderDao()

    @Provides
    fun provideMediaItemDao(db: MediaDatabase) = db.mediaItemDao()

    @Provides
    fun provideThumbnailDao(db: MediaDatabase) = db.thumbnailDao()

    @Provides
    fun provideVideoTrackDao(db: MediaDatabase) = db.videoTrackDao()

    @Provides
    fun provideAudioTrackDao(db: MediaDatabase) = db.audioTrackDao()

    @Provides
    fun provideSubtitleTrackDao(db: MediaDatabase) = db.subtitleTrackDao()

}