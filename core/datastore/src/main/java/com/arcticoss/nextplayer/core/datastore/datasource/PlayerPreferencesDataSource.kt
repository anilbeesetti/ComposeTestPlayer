package com.arcticoss.nextplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.arcticoss.model.PlayerUiPreferences
import javax.inject.Inject

class PlayerPreferencesDataSource @Inject constructor(
    private val playerUiPreferences: DataStore<PlayerUiPreferences>
) {

    val uiPrefStream = playerUiPreferences.data

    suspend fun updateUiPref(uiPref: PlayerUiPreferences) {
        playerUiPreferences.updateData {
            it.copy(
                brightnessLevel = uiPref.brightnessLevel
            )
        }
    }

}