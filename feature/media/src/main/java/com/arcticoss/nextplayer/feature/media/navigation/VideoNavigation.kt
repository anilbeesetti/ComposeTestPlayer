package com.arcticoss.nextplayer.feature.media.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.arcticoss.nextplayer.feature.media.screens.video.VideosScreen

const val videosScreenNavigationRoute = "videos_screen_route"
internal const val folderIdArg = "folderId"

fun NavController.navigateToVideosScreen(folderId: Long, navOptions: NavOptions? = null) {
    this.navigate("$videosScreenNavigationRoute/$folderId", navOptions)
}

fun NavGraphBuilder.videoScreen(
    onBackClick: () -> Unit,
    onMediaItemClick: (mediaId: Long, folderId: Long) -> Unit
) {
    composable(
        route = "$videosScreenNavigationRoute/{$folderIdArg}",
        arguments = listOf(
            navArgument(folderIdArg) { type = NavType.LongType}
        )
    ) {
        VideosScreen(
            onNavItemClick = onBackClick,
            onMediaItemClick = onMediaItemClick
        )
    }
}