package com.arcticoss.nextplayer.feature.player.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.android.exoplayer2.text.CueGroup

@Composable
fun Subtitle(
    cueGroup: CueGroup,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        cueGroup.cues.forEach {
            Text(
                text = it.text.toString(),
                textAlign = TextAlign.Center
            )
            it.textAlignment
        }
    }
}