package com.arcticoss.nextplayer.core.datastore.datasource

import androidx.datastore.core.DataStore
import com.arcticoss.nextplayer.core.model.InterfacePreferences
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import com.arcticoss.nextplayer.core.model.Theme
import javax.inject.Inject

class InterfacePreferencesDataSource @Inject constructor(
    private val interfacePreferences: DataStore<InterfacePreferences>
) {

    val preferencesFlow = interfacePreferences.data

    suspend fun updatePreferences(preferences: InterfacePreferences) {
        interfacePreferences.updateData { preferences }
    }

    suspend fun updateTheme(theme: Theme) {
        interfacePreferences.updateData {
            it.copy(theme = theme)
        }
    }

    suspend fun updateSortBy(sortBy: SortBy) {
        interfacePreferences.updateData {
            it.copy(sortBy = sortBy)
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        interfacePreferences.updateData {
            it.copy(sortOrder = sortOrder)
        }
    }

    suspend fun toggleShowFloatingButton() {
        interfacePreferences.updateData {
            it.copy(showFloatingButton = !it.showFloatingButton)
        }
    }

    suspend fun toggleShowHidden() {
        interfacePreferences.updateData {
            it.copy(showHidden = !it.showHidden)
        }
    }

    suspend fun toggleGroupVideos() {
        interfacePreferences.updateData {
            it.copy(groupVideos = !it.groupVideos)
        }
    }
}