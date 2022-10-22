package com.arcticoss.nextplayer.feature.media.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.media.settings.list.InterfacePreferencesScreen

const val interfacePreferencesNavigationRoute = "interface_preferences_route"

fun NavController.navigateToInterfacePreferences(navOptions: NavOptions? = null) {
    this.navigate(interfacePreferencesNavigationRoute, navOptions)
}

fun NavGraphBuilder.interfacePreferencesScreen(onBackClick: () -> Unit) {
    composable(route = interfacePreferencesNavigationRoute) {
        InterfacePreferencesScreen(
            onBackClick = onBackClick
        )
    }
}