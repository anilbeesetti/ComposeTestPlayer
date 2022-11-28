package com.arcticoss.nextplayer.core.data.repository

import com.arcticoss.nextplayer.core.datastore.datasource.UiPreferencesDataSource
import com.arcticoss.nextplayer.core.model.SortBy
import com.arcticoss.nextplayer.core.model.SortOrder
import com.arcticoss.nextplayer.core.model.Theme
import com.arcticoss.nextplayer.core.model.UiPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUiPreferencesRepository @Inject constructor(
    private val uiPreferencesDataSource: UiPreferencesDataSource
) : UiPreferencesRepository {

    override val preferencesFlow: Flow<UiPreferences> =
        uiPreferencesDataSource.uiPreferencesFlow

    override suspend fun updatePreferences(preferences: UiPreferences) =
        uiPreferencesDataSource.updatePreferences(preferences)

    override suspend fun setTheme(theme: Theme) =
        uiPreferencesDataSource.updateTheme(theme)

    override suspend fun setSortBy(sortBy: SortBy) =
        uiPreferencesDataSource.updateSortBy(sortBy)

    override suspend fun setSortOrder(sortOrder: SortOrder) =
        uiPreferencesDataSource.updateSortOrder(sortOrder)

    override suspend fun toggleShowFloatingButton() =
        uiPreferencesDataSource.toggleShowFloatingButton()

    override suspend fun toggleShowHidden() =
        uiPreferencesDataSource.toggleShowHidden()

    override suspend fun toggleGroupVideos() =
        uiPreferencesDataSource.toggleGroupVideos()
}