package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerUIMainControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconRippleButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Rounded.SkipPrevious,
                contentDescription = "",
                modifier = Modifier.size(32.dp)
            )
        }
        IconRippleButton(onClick = { onPlayPauseClick() }) {
            Icon(
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = "",
                modifier = Modifier.size(48.dp)
            )
        }
        IconRippleButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = "",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun IconRippleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(20.dp),
        content = { content() }
    )
}