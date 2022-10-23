package com.arcticoss.nextplayer.feature.media.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconTextButton(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}