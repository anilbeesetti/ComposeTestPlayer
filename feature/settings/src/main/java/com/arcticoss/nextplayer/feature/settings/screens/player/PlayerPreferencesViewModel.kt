package com.arcticoss.nextplayer.feature.settings.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.data.repository.PlayerPreferencesRepository
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerPreferencesViewModel @Inject constructor(
    private val preferencesRepository: PlayerPreferencesRepository
) : ViewModel() {

    val preferencesFlow = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlayerPreferences()
        )

    fun toggleSaveBrightnessLevel() {
        viewModelScope.launch {
            preferencesRepository.toggleSaveBrightness()
        }
    }

    fun toggleFastSeeking() {
        viewModelScope.launch {
            preferencesRepository.toggleFastSeeking()
        }
    }
}
