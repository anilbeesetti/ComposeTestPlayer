package com.arcticoss.nextplayer.navigation

import android.content.Context
import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.arcticoss.nextplayer.feature.player.PlayerActivity
import com.arcticoss.nextplayer.feature.media.navigation.mediaScreen
import com.arcticoss.nextplayer.feature.media.navigation.mediaScreenNavigationRoute
import com.arcticoss.nextplayer.feature.media.navigation.navigateToVideosScreen
import com.arcticoss.nextplayer.feature.media.navigation.videoScreen
import com.arcticoss.nextplayer.feature.media.screens.media.NavigateTo
import com.arcticoss.nextplayer.feature.settings.navigation.navigateToSettings


const val MEDIA_ROUTE = "media_nav_route"

fun NavGraphBuilder.mediaNavGraph(
    context: Context,
    navController: NavHostController
) {
    navigation(
        startDestination = mediaScreenNavigationRoute,
        route = MEDIA_ROUTE
    ) {
        mediaScreen(
            onNavigate = { navigateTo ->
                when (navigateTo) {
                    NavigateTo.Settings -> navController.navigateToSettings()
                    is NavigateTo.Player -> navigateToPlayerScreen(context, navigateTo.mediaId)
                    is NavigateTo.Videos -> navController.navigateToVideosScreen(navigateTo.folderId)
                }
            }
        )
        videoScreen(
            onBackClick = { navController.popBackStack() },
            onMediaItemClick = { mediaId, folderId ->
                navigateToPlayerScreen(context, mediaId, folderId)
            }
        )
    }
}


fun navigateToPlayerScreen(context: Context, mediaId: Long, folderId: Long = 0) {
    val intent = Intent(context, PlayerActivity::class.java).also {
        it.putExtra("mediaID", mediaId)
        it.putExtra("folderID", folderId)
    }
    context.startActivity(intent)
}