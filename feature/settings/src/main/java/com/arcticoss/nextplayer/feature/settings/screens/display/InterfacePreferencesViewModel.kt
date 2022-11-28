package com.arcticoss.nextplayer.feature.settings.screens.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.data.repository.UiPreferencesRepository
import com.arcticoss.nextplayer.core.model.UiPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterfacePreferencesViewModel @Inject constructor(
    private val preferencesRepository: UiPreferencesRepository
) : ViewModel() {

    val preferencesFlow = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiPreferences()
        )

    fun toggleFloatingButton() {
        viewModelScope.launch {
            preferencesRepository.toggleShowFloatingButton()
        }
    }

    fun toggleShowHidden() {
        viewModelScope.launch {
            preferencesRepository.toggleShowHidden()
        }
    }

    fun toggleGroupVideos() {
        viewModelScope.launch {
            preferencesRepository.toggleGroupVideos()
        }
    }
}

