package com.arcticoss.nextplayer.feature.settings.screens.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.datastore.datasource.UiPreferencesDataSource
import com.arcticoss.nextplayer.core.model.UiPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterfacePreferencesViewModel @Inject constructor(
    private val preferencesDataSource: UiPreferencesDataSource
) : ViewModel() {

    val preferencesFlow = preferencesDataSource
        .uiPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiPreferences()
        )

    fun toggleFloatingButton() {
        viewModelScope.launch {
            preferencesDataSource.toggleShowFloatingButton()
        }
    }

    fun toggleShowHidden() {
        viewModelScope.launch {
            preferencesDataSource.toggleShowHidden()
        }
    }

    fun toggleGroupVideos() {
        viewModelScope.launch {
            preferencesDataSource.toggleGroupVideos()
        }
    }
}

