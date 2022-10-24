package com.arcticoss.nextplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.arcticoss.model.AspectRatio
import com.arcticoss.model.PlayerPreferences
import com.arcticoss.model.next
import javax.inject.Inject

class PlayerPreferencesDataSource @Inject constructor(
    private val playerPreferences: DataStore<PlayerPreferences>
) {

    val preferencesFlow = playerPreferences.data

    suspend fun updatePreferences(preferences: PlayerPreferences) {
        playerPreferences.updateData { preferences }
    }

    suspend fun toggleSaveBrightness() {
        playerPreferences.updateData {
            it.copy(saveBrightnessLevel = !it.saveBrightnessLevel)
        }
    }

    suspend fun toggleSavePlayBackSpeed() {
        playerPreferences.updateData {
            it.copy(savePlayBackSpeed = !it.savePlayBackSpeed)
        }
    }

    suspend fun toggleFastSeeking() {
        playerPreferences.updateData {
            it.copy(fastSeeking = !it.fastSeeking)
        }
    }

    suspend fun updateBrightnessLevel(brightnessLevel: Int) {
        playerPreferences.updateData {
            it.copy(brightnessLevel = brightnessLevel)
        }
    }

    suspend fun switchAspectRatio() {
        playerPreferences.updateData {
            it.copy(aspectRatio = it.aspectRatio.next())
        }
    }

    suspend fun changeAspectRatio(aspectRatio: AspectRatio) {
        playerPreferences.updateData {
            it.copy(aspectRatio = aspectRatio)
        }
    }
}