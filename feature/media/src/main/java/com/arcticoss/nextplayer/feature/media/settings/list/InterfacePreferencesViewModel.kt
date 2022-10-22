package com.arcticoss.nextplayer.feature.media.settings.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.model.InterfacePreferences
import com.arcticoss.nextplayer.core.datastore.datasource.InterfacePreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterfacePreferencesViewModel @Inject constructor(
    private val interfacePreferencesDataSource: InterfacePreferencesDataSource
) : ViewModel() {

    val interfacePreferences = interfacePreferencesDataSource
        .interfacePreferencesStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InterfacePreferences()
        )

    fun toggleFloatingButton(){
        viewModelScope.launch {
            interfacePreferencesDataSource.toggleShowFloatingButton()
        }
    }

    fun toggleShowHidden() {
        viewModelScope.launch {
            interfacePreferencesDataSource.toggleShowHidden()
        }
    }

    fun toggleGroupVideos() {
        viewModelScope.launch {
            interfacePreferencesDataSource.toggleGroupVideos()
        }
    }

}

