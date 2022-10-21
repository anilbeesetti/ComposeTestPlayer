package com.arcticoss.nextplayer.feature.media.settings

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.arcticoss.nextplayer.feature.media.R

data class SettingGroup(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val icon: ImageVector
)

val settingGroupList: List<SettingGroup> = listOf(
    SettingGroup(
        title = R.string.list,
        description = R.string.list_description,
        icon = Icons.Rounded.List
    ),
    SettingGroup(
        title = R.string.player,
        description = R.string.player_description,
        icon = Icons.Rounded.PlayArrow
    ),
    SettingGroup(
        title = R.string.about,
        description = R.string.about_description,
        icon = Icons.Rounded.Info
    )
)
