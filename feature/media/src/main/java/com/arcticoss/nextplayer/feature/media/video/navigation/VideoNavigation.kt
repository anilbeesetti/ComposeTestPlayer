package com.arcticoss.nextplayer.feature.media.video.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.media.MediaScreen

const val videosNavigationRoute = "videos_route"

fun NavController.navigateToVideos(navOptions: NavOptions? = null) {
    this.navigate(videosNavigationRoute, navOptions)
}

fun NavGraphBuilder.videosScreen(navController: NavHostController) {
    composable(route = videosNavigationRoute) {
        MediaScreen(navController = navController)
    }
}