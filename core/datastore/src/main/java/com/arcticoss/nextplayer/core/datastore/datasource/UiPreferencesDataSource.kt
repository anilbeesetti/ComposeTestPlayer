package com.arcticoss.nextplayer.core.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import com.arcticoss.nextplayer.core.model.UiPreferences
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import com.arcticoss.nextplayer.core.model.Theme
import java.io.IOException
import javax.inject.Inject

class UiPreferencesDataSource @Inject constructor(
    private val uiPreferences: DataStore<UiPreferences>
) {

    val uiPreferencesFlow = uiPreferences.data

    suspend fun updatePreferences(preferences: UiPreferences) {
        try {
            uiPreferences.updateData { preferences }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }

    suspend fun updateTheme(theme: Theme) {
        try {
            uiPreferences.updateData { it.copy(theme = theme) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }

    suspend fun updateSortBy(sortBy: SortBy) {
        try {
            uiPreferences.updateData { it.copy(sortBy = sortBy) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        try {
            uiPreferences.updateData { it.copy(sortOrder = sortOrder) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }

    suspend fun toggleShowFloatingButton() {
        try {
            uiPreferences.updateData { it.copy(showFloatingButton = !it.showFloatingButton) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }

    suspend fun toggleShowHidden() {
        try {
            uiPreferences.updateData { it.copy(showHidden = !it.showHidden) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }

    suspend fun toggleGroupVideos() {
        try {
            uiPreferences.updateData { it.copy(groupVideos = !it.groupVideos) }
        } catch (ioException: IOException) {
            Log.e("NextPlayerPreferences", "Failed to update ui preferences", ioException)
        }
    }
}