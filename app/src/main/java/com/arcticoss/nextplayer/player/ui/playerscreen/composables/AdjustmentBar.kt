package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AdjustmentBar(
    value: Int,
    maxValue: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val progress = (1f / maxValue) * value
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.extraSmall)
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = value.toString())
        Spacer(modifier = Modifier.height(10.dp))
        LinearVerticalProgressIndicator(
            modifier = Modifier.weight(1f),
            progress = progress
        )
        Spacer(modifier = Modifier.height(10.dp))
        Icon(imageVector = icon, contentDescription = icon.name)
    }
}