package com.arcticoss.nextplayer.feature.media.settings

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Aod
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.arcticoss.nextplayer.feature.media.R

data class SettingGroup(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val icon: ImageVector,
    val navigateTo: SettingsNavigateTo
)

val settingGroupList: List<SettingGroup> = listOf(
    SettingGroup(
        title = R.string.interface_name,
        description = R.string.interface_description,
        icon = Icons.Rounded.Aod,
        navigateTo = SettingsNavigateTo.Interface
    ),
    SettingGroup(
        title = R.string.player,
        description = R.string.player_description,
        icon = Icons.Rounded.PlayArrow,
        navigateTo = SettingsNavigateTo.Player
    ),
    SettingGroup(
        title = R.string.about,
        description = R.string.about_description,
        icon = Icons.Rounded.Info,
        navigateTo = SettingsNavigateTo.About
    )
)



enum class SettingsNavigateTo {
    Interface, Player, About
}
