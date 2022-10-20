package com.arcticoss.feature.player.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.PlayerViewModel
import com.arcticoss.feature.player.utils.findActivity
import com.arcticoss.feature.player.utils.setBrightness

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun EffectsHandler(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val uiPreferences by viewModel.uiPreferencesFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current


    LaunchedEffect(uiPreferences.brightnessLevel) {
        val activity = context.findActivity()
        val level = 1.0f / playerState.maxLevel * uiPreferences.brightnessLevel
        activity?.setBrightness(level)
    }
}