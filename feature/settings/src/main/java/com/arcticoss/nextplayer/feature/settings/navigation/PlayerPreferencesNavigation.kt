package com.arcticoss.nextplayer.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.settings.screens.player.PlayerPreferencesScreen

const val playerPreferencesNavigationRoute = "player_preferences_route"

fun NavController.navigateToPlayerPreferences(navOptions: NavOptions? = null) {
    this.navigate(playerPreferencesNavigationRoute, navOptions)
}

fun NavGraphBuilder.playerPreferencesScreen(onBackClick: () -> Unit) {
    composable(route = playerPreferencesNavigationRoute) {
        PlayerPreferencesScreen(
            onBackClick = onBackClick
        )
    }
}