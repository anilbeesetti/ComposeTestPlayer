package com.arcticoss.nextplayer.core.data.repository

import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import com.arcticoss.nextplayer.core.model.Theme
import com.arcticoss.nextplayer.core.model.UiPreferences
import kotlinx.coroutines.flow.Flow

interface UiPreferencesRepository {

    /**
     * Stream of [UiPreferences]
     */
    val uiPreferencesStream: Flow<UiPreferences>

    /**
     * Updates [UiPreferences]
     */
    suspend fun updatePreferences(preferences: UiPreferences)

    /**
     * Sets the desired theme
     */
    suspend fun setTheme(theme: Theme)

    /**
     * Sets the desired [SortBy]
     */
    suspend fun setSortBy(sortBy: SortBy)

    /**
     * Sets the desired [SortOrder]
     */
    suspend fun setSortOrder(sortOrder: SortOrder)

    /**
     * Toggles show floating button or not
     */
    suspend fun toggleShowFloatingButton()

    /**
     * Toggles whether should show hidden or not
     */
    suspend fun toggleShowHidden()

    /**
     * Toggles whether to group videos or not
     */
    suspend fun toggleGroupVideos()
}