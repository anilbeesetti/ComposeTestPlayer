package com.arcticoss.nextplayer.feature.media.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.media.screens.media.MediaScreen
import com.arcticoss.nextplayer.feature.media.screens.media.NavigateTo

const val mediaScreenNavigationRoute = "media_screen_route"

fun NavController.navigateToMediaScreen(navOptions: NavOptions? = null) {
    this.navigate(mediaScreenNavigationRoute, navOptions)
}

fun NavGraphBuilder.mediaScreen(onNavigate: (NavigateTo) -> Unit) {
    composable(route = mediaScreenNavigationRoute) {
        MediaScreen(
            onNavigate = onNavigate
        )
    }
}