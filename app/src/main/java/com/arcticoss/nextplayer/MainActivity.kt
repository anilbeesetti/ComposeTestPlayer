package com.arcticoss.nextplayer

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
import com.arcticoss.nextplayer.feature.media.MediaScreen
import com.arcticoss.nextplayer.feature.media.settings.navigation.settingsScreen
import com.arcticoss.nextplayer.feature.media.video.navigation.videosNavigationRoute
import com.arcticoss.nextplayer.feature.media.video.navigation.videosScreen
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
        startDestination = videosNavigationRoute
    ) {
        videosScreen(navController)
        settingsScreen(navController)
    }
}

