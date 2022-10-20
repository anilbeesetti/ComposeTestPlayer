package com.arcticoss.nextplayer.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.arcticoss.model.PlayerUiPreferences
import com.arcticoss.nextplayer.core.datastore.PlayerUiPreferencesSerializer
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


private const val PLAYER_UI_PREFERENCES_DATASTORE_FILE = "player_ui_preferences.json"


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    fun providePlayerUiPreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<PlayerUiPreferences> {
        return DataStoreFactory.create(
            serializer = PlayerUiPreferencesSerializer,
            produceFile = { appContext.dataStoreFile(PLAYER_UI_PREFERENCES_DATASTORE_FILE) }
        )
    }
}