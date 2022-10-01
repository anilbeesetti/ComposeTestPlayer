package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arcticoss.nextplayer.player.utils.TimeUtils

@Composable
fun PlayerUIFooter(
    duration: Long,
    currentPosition: Long,
    onSeek: (Float) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = TimeUtils.formatTime(context, currentPosition),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Slider(
                    value = currentPosition.toFloat(),
                    valueRange = 0f..duration.toFloat(),
                    onValueChange = { onSeek(it) }
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = TimeUtils.formatTime(context, duration),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = "")
            }
        }
    }
}