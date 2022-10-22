package com.arcticoss.nextplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.arcticoss.feature.player.PlayerActivity
import com.arcticoss.nextplayer.feature.media.NavigateTo
import com.arcticoss.nextplayer.feature.media.settings.SettingsNavigateTo
import com.arcticoss.nextplayer.feature.media.settings.navigation.interfacePreferencesScreen
import com.arcticoss.nextplayer.feature.media.settings.navigation.navigateToInterfacePreferences
import com.arcticoss.nextplayer.feature.media.settings.navigation.navigateToSettings
import com.arcticoss.nextplayer.feature.media.settings.navigation.settingsScreen
import com.arcticoss.nextplayer.feature.media.video.navigation.mediaNavigationRoute
import com.arcticoss.nextplayer.feature.media.video.navigation.mediaScreen
import com.arcticoss.nextplayer.ui.theme.NextPlayerTheme
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NextPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NextPlayerNavHost(navController = rememberNavController())
                }
            }
        }
    }
}

@Composable
fun NextPlayerNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = mediaNavigationRoute
    ) {
        mediaScreen(
            onNavigate = { navigateTo ->
                when (navigateTo) {
                    NavigateTo.Settings -> navController.navigateToSettings()
                    is NavigateTo.Player -> {}
                }
            }
        )
        settingsScreen(
            onNavigate = { navigateTo ->
                when (navigateTo) {
                    SettingsNavigateTo.Interface -> navController.navigateToInterfacePreferences()
                    SettingsNavigateTo.Player -> TODO()
                    SettingsNavigateTo.About -> TODO()
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
        interfacePreferencesScreen(
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}


fun startPlayerActivity(context: Context, path: String) {
    val intent = Intent(context, PlayerActivity::class.java).also {
        it.putExtra("videoFilePath", path)
    }
    context.startActivity(intent)
}

