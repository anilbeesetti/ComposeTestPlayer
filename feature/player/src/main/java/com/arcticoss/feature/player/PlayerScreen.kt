package com.arcticoss.feature.player

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.feature.player.presentation.composables.*
import com.arcticoss.feature.player.utils.*
import com.arcticoss.model.PlayerPreferences
import com.google.android.exoplayer2.ExoPlayer


private const val TAG = "NextPlayerScreen"


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayerScreen(
    onBackPressed: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val player = viewModel.player
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()
    val playerCurrentPosition by viewModel.playerCurrentPosition.collectAsStateWithLifecycle()

    EventHandler(
        playerState = playerState,
        playerUiState = playerUiState,
        onEvent = viewModel::onEvent,
        onUiEvent = viewModel::onUiEvent
    )
    PlayerScreen(
        player = player,
        playerState = playerState,
        playerUiState = playerUiState,
        currentPosition = playerCurrentPosition,
        preferences = preferences,
        onEvent = viewModel::onEvent,
        onUiEvent = viewModel::onUiEvent,
        onBackPressed = onBackPressed
    )
}


@Composable
internal fun PlayerScreen(
    player: ExoPlayer,
    currentPosition: Long,
    playerState: PlayerState,
    playerUiState: PlayerUiState,
    preferences: PlayerPreferences,
    onEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NextExoPlayer(
            exoPlayer = player,
            playWhenReady = playerState.playWhenReady,
            aspectRatio = preferences.aspectRatio,
            onBackPressed = onBackPressed,
            onEvent = onEvent
        )
        PlayerGestures(
            player = player,
            onUiEvent = onUiEvent,
            onEvent = onEvent
        )
        NextPlayerUI(
            player = player,
            playerState = playerState,
            playerUiState = playerUiState,
            currentPosition = currentPosition,
            preferences = preferences,
            onBackPressed = onBackPressed,
            onUiEvent = onUiEvent
        )
    }
}