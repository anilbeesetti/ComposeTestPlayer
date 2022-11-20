package com.arcticoss.nextplayer.feature.settings.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.core.datastore.datasource.PlayerPreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerPreferencesViewModel @Inject constructor(
    private val preferencesDataSource: PlayerPreferencesDataSource
): ViewModel() {

    val preferencesFlow = preferencesDataSource
        .preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlayerPreferences()
        )

    fun toggleSaveBrightnessLevel() {
        viewModelScope.launch {
            preferencesDataSource.toggleSaveBrightness()
        }
    }

    fun toggleFastSeeking() {
        viewModelScope.launch {
            preferencesDataSource.toggleFastSeeking()
        }
    }
}
