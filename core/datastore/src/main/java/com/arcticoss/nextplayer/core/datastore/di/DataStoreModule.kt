package com.arcticoss.nextplayer.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.arcticoss.nextplayer.core.datastore.serializer.InterfacePreferencesSerializer
import com.arcticoss.nextplayer.core.datastore.serializer.PlayerPreferencesSerializer
import com.arcticoss.nextplayer.core.model.InterfacePreferences
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private const val INTERFACE_PREFERENCES_DATASTORE_FILE = "interface_preferences.json"
private const val PLAYER_PREFERENCES_DATASTORE_FILE = "player_preferences.json"


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideInterfacePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<InterfacePreferences> {
        return DataStoreFactory.create(
            serializer = InterfacePreferencesSerializer,
            produceFile = { appContext.dataStoreFile(INTERFACE_PREFERENCES_DATASTORE_FILE) }
        )
    }

    @Provides
    @Singleton
    fun providePlayerPreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<PlayerPreferences> {
        return DataStoreFactory.create(
            serializer = PlayerPreferencesSerializer,
            produceFile = { appContext.dataStoreFile(PLAYER_PREFERENCES_DATASTORE_FILE) }
        )
    }

}