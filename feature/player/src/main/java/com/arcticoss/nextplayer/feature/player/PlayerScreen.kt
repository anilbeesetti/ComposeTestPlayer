package com.arcticoss.nextplayer.feature.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.core.model.PlayerPreferences
import com.arcticoss.nextplayer.feature.player.presentation.composables.*
import com.google.android.exoplayer2.ExoPlayer


private const val TAG = "NextPlayerScreen"


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayerScreen(
    onBackPressed: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val player = viewModel.playerHelper.exoPlayer
    val playerState by viewModel.exoplayerStateStateFlow.collectAsStateWithLifecycle()
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val playerUiState by viewModel.playerUiState.collectAsStateWithLifecycle()
    val playerCurrentPosition by viewModel.playerCurrentPosition.collectAsStateWithLifecycle()

    EventHandler(
        exoPlayerState = playerState,
        playerUiState = playerUiState,
        onEvent = viewModel::onEvent,
        onUiEvent = viewModel::onUiEvent
    )
    PlayerScreen(
        player = player,
        exoPlayerState = playerState,
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
    exoPlayerState: ExoplayerState,
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
            exoPlayerState = exoPlayerState,
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
            exoPlayerState = exoPlayerState,
            playerUiState = playerUiState,
            currentPosition = currentPosition,
            preferences = preferences,
            onBackPressed = onBackPressed,
            onUiEvent = onUiEvent
        )

        if (playerUiState.isAudioTrackDialogVisible) {
            CenterDialog(
                onDismiss = { onUiEvent(UiEvent.ShowAudioTrackDialog(false)) },
                title = { Text(text = "Select audio track") },
                content = {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(Modifier.selectableGroup()) {
                            exoPlayerState.audioTracks.forEach {
                                AudioTrackChooser(
                                    text = it.displayName,
                                    selected = it.isSelected,
                                    onClick = { onEvent(PlayerEvent.SwitchAudioTrack(it.formatId)) }
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CenterDialog(
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = modifier
                    .fillMaxWidth(0.70f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                    ) {
                        val textStyle = MaterialTheme.typography.headlineSmall
                        ProvideTextStyle(value = textStyle) {
                            title()
                        }
                    }
                    content()
                }
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    )
}


@Composable
fun AudioTrackChooser(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}