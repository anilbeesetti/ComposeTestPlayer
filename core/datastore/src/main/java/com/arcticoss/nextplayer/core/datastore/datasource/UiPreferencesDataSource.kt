package com.arcticoss.nextplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.arcticoss.nextplayer.core.model.UiPreferences
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import com.arcticoss.nextplayer.core.model.Theme
import javax.inject.Inject

class UiPreferencesDataSource @Inject constructor(
    private val uiPreferences: DataStore<UiPreferences>
) {

    val uiPreferencesFlow = uiPreferences.data

    suspend fun updatePreferences(preferences: UiPreferences) {
        uiPreferences.updateData { preferences }
    }

    suspend fun updateTheme(theme: Theme) {
        uiPreferences.updateData {
            it.copy(theme = theme)
        }
    }

    suspend fun updateSortBy(sortBy: SortBy) {
        uiPreferences.updateData {
            it.copy(sortBy = sortBy)
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        uiPreferences.updateData {
            it.copy(sortOrder = sortOrder)
        }
    }

    suspend fun toggleShowFloatingButton() {
        uiPreferences.updateData {
            it.copy(showFloatingButton = !it.showFloatingButton)
        }
    }

    suspend fun toggleShowHidden() {
        uiPreferences.updateData {
            it.copy(showHidden = !it.showHidden)
        }
    }

    suspend fun toggleGroupVideos() {
        uiPreferences.updateData {
            it.copy(groupVideos = !it.groupVideos)
        }
    }
}