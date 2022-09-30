package com.arcticoss.nextplayer.player.ui.playerscreen.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerUIFooter() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "00:00:00", color = Color.White)
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier.weight(1f)
        ) {
            Slider(value = 0F, onValueChange = {})
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "03:00:00", color = Color.White)
    }
}