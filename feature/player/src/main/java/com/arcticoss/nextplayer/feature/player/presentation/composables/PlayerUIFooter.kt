package com.arcticoss.nextplayer.feature.player.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.FitScreen
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.feature.player.PlayerUiState
import com.arcticoss.nextplayer.feature.player.utils.TimeUtils
import com.arcticoss.model.AspectRatio
import com.arcticoss.model.PlayerPreferences

@Composable
fun PlayerUIFooter(
    duration: Long,
    playerUiState: PlayerUiState,
    currentPosition: Long,
    modifier: Modifier = Modifier,
    onLockClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekFinished: () -> Unit = {},
    onAspectRatioClick: () -> Unit,
    preferences: PlayerPreferences
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        if (playerUiState.isControllerVisible || playerUiState.isSeekBarVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeUtils.formatTime(context, currentPosition),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Slider(
                        value = currentPosition.toFloat(),
                        valueRange = 0f..duration.toFloat(),
                        onValueChange = onSeek,
                        onValueChangeFinished = onSeekFinished
                    )
                }
                Text(
                    text = TimeUtils.formatTime(context, duration),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
        }
        if (playerUiState.isControllerVisible) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Rounded.Lock, contentDescription = "")
                }
                Row {
                    IconButton(onClick = onAspectRatioClick) {
                        Icon(
                            imageVector = when(preferences.aspectRatio) {
                                AspectRatio.FitScreen -> Icons.Rounded.FitScreen
                                AspectRatio.Stretch -> Icons.Rounded.AspectRatio
                                AspectRatio.Crop -> Icons.Rounded.Crop
                            },
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}