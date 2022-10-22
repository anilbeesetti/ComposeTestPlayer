package com.arcticoss.nextplayer.feature.settings.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arcticoss.nextplayer.feature.settings.R
import com.arcticoss.nextplayer.feature.settings.composables.ClickablePreferenceItem
import com.arcticoss.nextplayer.feature.settings.composables.PreferenceSwitch

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InterfacePreferencesScreen(
    onBackClick: () -> Unit,
    viewModel: InterfacePreferencesViewModel = hiltViewModel()
) {
    val interfacePreferences by viewModel.interfacePreferences.collectAsStateWithLifecycle()

    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.interface_name)
                    )
                },
                scrollBehavior = scrollBehaviour,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                PreferenceGroupTitle(text = stringResource(id = R.string.appearance))
            }
            item {
                ClickablePreferenceItem(
                    title = stringResource(id = R.string.theme),
                    description = interfacePreferences.theme.name,
                    onClick = { }
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.floating_button),
                    description = stringResource(id = R.string.floating_button_description),
                    isChecked = interfacePreferences.showFloatingButton,
                    onClick = viewModel::toggleFloatingButton
                )
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.group_videos),
                    description = stringResource(id = R.string.group_videos_description),
                    isChecked = interfacePreferences.groupVideos,
                    onClick = viewModel::toggleGroupVideos
                )
            }
            item {
                PreferenceGroupTitle(text = stringResource(id = R.string.scan))
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.show_hidden),
                    description = stringResource(id = R.string.show_hidden_description),
                    isChecked = interfacePreferences.showHidden,
                    onClick = viewModel::toggleShowHidden
                )
            }
        }
    }
}

@Composable
fun PreferenceGroupTitle(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 28.dp, bottom = 12.dp),
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}