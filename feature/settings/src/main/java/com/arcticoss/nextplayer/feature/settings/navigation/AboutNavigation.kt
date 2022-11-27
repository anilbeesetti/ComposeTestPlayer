package com.arcticoss.nextplayer.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.settings.screens.about.AboutScreen

const val aboutNavigationRoute = "about_route"

fun NavController.navigateToAboutScreen(navOptions: NavOptions? = null) {
    this.navigate(aboutNavigationRoute, navOptions)
}

fun NavGraphBuilder.aboutScreen(onBackClick: () -> Unit) {
    composable(route = aboutNavigationRoute) {
        AboutScreen(
            onBackClick = onBackClick
        )
    }
}