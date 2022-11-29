package com.arcticoss.nextplayer.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CenterDialog(
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = modifier
                    .fillMaxWidth(0.70f)
                    .padding(bottom = 20.dp),
                tonalElevation = tonalElevation
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