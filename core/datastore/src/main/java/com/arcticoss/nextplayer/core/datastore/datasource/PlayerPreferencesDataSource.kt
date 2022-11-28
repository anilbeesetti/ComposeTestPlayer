package com.arcticoss.nextplayer.core.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.model.ResizeMode
import com.arcticoss.nextplayer.core.model.next
import java.io.IOException
import javax.inject.Inject

class PlayerPreferencesDataSource @Inject constructor(
    private val playerPreferences: DataStore<PlayerPreferences>
) {

    val playerPreferencesFlow = playerPreferences.data

    suspend fun updatePreferences(preferences: PlayerPreferences) {
        try {
            playerPreferences.updateData { preferences }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update player preferences", ioException)
        }
    }

    suspend fun toggleSaveBrightness() {
        try {
            playerPreferences.updateData { it.copy(saveBrightnessLevel = !it.saveBrightnessLevel) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update player preferences", ioException)
        }
    }

    suspend fun toggleSavePlayBackSpeed() {
        playerPreferences.updateData { it.copy(savePlayBackSpeed = !it.savePlayBackSpeed) }
    }

    suspend fun toggleFastSeeking() {
        try {
            playerPreferences.updateData { it.copy(fastSeeking = !it.fastSeeking) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update player preferences", ioException)
        }
    }

    suspend fun updateBrightnessLevel(brightnessLevel: Int) {
        try {
            playerPreferences.updateData { it.copy(brightnessLevel = brightnessLevel) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update player preferences", ioException)
        }
    }

    suspend fun switchAspectRatio() {
        try {
            playerPreferences.updateData { it.copy(resizeMode = it.resizeMode.next()) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update player preferences", ioException)
        }
    }

    suspend fun changeAspectRatio(resizeMode: ResizeMode) {
        try {
            playerPreferences.updateData { it.copy(resizeMode = resizeMode) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update player preferences", ioException)
        }
    }
}