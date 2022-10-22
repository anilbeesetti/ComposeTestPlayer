package com.arcticoss.nextplayer.feature.media.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.arcticoss.nextplayer.feature.media.R
import com.arcticoss.nextplayer.feature.media.settings.composables.SettingGroupItem
import com.arcticoss.nextplayer.feature.media.video.composables.MediaLargeTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (SettingsNavigateTo) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            MediaLargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings_screen)
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
            items(settingGroupList) { settingGroup ->
                SettingGroupItem(
                    title = stringResource(id = settingGroup.title),
                    description = stringResource(id = settingGroup.description),
                    icon = settingGroup.icon,
                    onClick = { onNavigate(settingGroup.navigateTo) }
                )
            }
        }
    }
}