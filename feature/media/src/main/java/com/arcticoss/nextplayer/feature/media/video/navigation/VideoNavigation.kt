package com.arcticoss.nextplayer.feature.media.video.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.media.MediaScreen
import com.arcticoss.nextplayer.feature.media.NavigateTo

const val mediaNavigationRoute = "media_route"

fun NavController.navigateToMediaScreen(navOptions: NavOptions? = null) {
    this.navigate(mediaNavigationRoute, navOptions)
}

fun NavGraphBuilder.mediaScreen(onNavigate: (NavigateTo) -> Unit) {
    composable(route = mediaNavigationRoute) {
        MediaScreen(
            onNavigate = onNavigate
        )
    }
}