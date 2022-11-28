package com.arcticoss.nextplayer.core.data.repository

import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.ResizeMode
import kotlinx.coroutines.flow.Flow

interface PlayerPreferencesRepository {

    /**
     * Stream of [PlayerPreferences]
     */
    val preferencesFlow: Flow<PlayerPreferences>

    /**
     * Updates the [PlayerPreferences]
     */
    suspend fun updatePreferences(preferences: PlayerPreferences)

    /**
     * Toggles whether save brightness level or not
     */
    suspend fun toggleSaveBrightness()

    /**
     * Toggles whether save playback speed or not
     */
    suspend fun toggleSavePlayBackSpeed()

    /**
     * Toggles whether to enable fast seek or not
     */
    suspend fun toggleFastSeeking()

    /**
     * Sets the brightness level
     */
    suspend fun setBrightnessLevel(brightnessLevel: Int)

    /**
     * Switches the [ResizeMode] to next one
     */
    suspend fun switchResizeMode()

    /**
     * Sets the desired [ResizeMode]
     */
    suspend fun setResizeMode(resizeMode: ResizeMode)
}
