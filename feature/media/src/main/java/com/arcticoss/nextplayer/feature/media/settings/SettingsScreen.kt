package com.arcticoss.nextplayer.feature.media.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.arcticoss.nextplayer.feature.media.R
import com.arcticoss.nextplayer.feature.media.settings.composables.SettingGroupItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column() {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
            LazyColumn() {
                item {
                    Text(
                        modifier = Modifier.padding(start = 24.dp, top = 48.dp, bottom = 24.dp),
                        text = stringResource(id = R.string.settings_screen),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                items(settingGroupList) { settingGroup ->
                    SettingGroupItem(
                        title = stringResource(id = settingGroup.title),
                        description = stringResource(id = settingGroup.description),
                        icon = settingGroup.icon,
                        onClick = {}
                    )
                }
            }
        }
    }
}

