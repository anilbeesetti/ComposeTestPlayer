package com.arcticoss.nextplayer.feature.settings.screens.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import com.arcticoss.nextplayer.core.model.InterfacePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterfacePreferencesViewModel @Inject constructor(
    private val preferencesDataSource: InterfacePreferencesDataSource
) : ViewModel() {

    val preferencesFlow = preferencesDataSource
        .preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InterfacePreferences()
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

