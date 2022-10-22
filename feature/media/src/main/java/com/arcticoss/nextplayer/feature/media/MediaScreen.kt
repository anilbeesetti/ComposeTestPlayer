package com.arcticoss.nextplayer.feature.media

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.media.video.composables.*


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    onNavigate: (NavigateTo) -> Unit,
    viewModel: MediaScreenViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            viewModel.syncMedia()
        }
    }
    val interfacePreferences by viewModel.interfacePreferences.collectAsStateWithLifecycle()

    CheckPermissionAndSetContent(
        title = {
            Text(
                text = stringResource(id = R.string.videos)
            )
        },
        navigationIcon = {
            IconButton(onClick = { onNavigate(NavigateTo.Settings) }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        }
    ) { innerPadding ->
        ShowVideoFiles(contentPadding = innerPadding)
    }
}

sealed interface NavigateTo {
    object Settings : NavigateTo
    data class Player(val path: String) : NavigateTo
}

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

