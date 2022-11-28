package com.arcticoss.nextplayer.core.data.repository

import com.arcticoss.nextplayer.core.datastore.datasource.PlayerPreferencesDataSource
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.ResizeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPlayerPreferencesRepository @Inject constructor(
    private val playerPreferencesDataSource: PlayerPreferencesDataSource
): PlayerPreferencesRepository {

    override val preferencesFlow: Flow<PlayerPreferences> =
        playerPreferencesDataSource.playerPreferencesFlow

    override suspend fun updatePreferences(preferences: PlayerPreferences) =
        playerPreferencesDataSource.updatePreferences(preferences)

    override suspend fun toggleSaveBrightness() =
        playerPreferencesDataSource.toggleSaveBrightness()

    override suspend fun toggleSavePlayBackSpeed() =
        playerPreferencesDataSource.toggleSavePlayBackSpeed()

    override suspend fun toggleFastSeeking() =
        playerPreferencesDataSource.toggleFastSeeking()

    override suspend fun setBrightnessLevel(brightnessLevel: Int) =
        playerPreferencesDataSource.updateBrightnessLevel(brightnessLevel)

    override suspend fun switchResizeMode() =
        playerPreferencesDataSource.switchResizeMode()

    override suspend fun setResizeMode(resizeMode: ResizeMode) =
        playerPreferencesDataSource.changeResizeMode(resizeMode)

}