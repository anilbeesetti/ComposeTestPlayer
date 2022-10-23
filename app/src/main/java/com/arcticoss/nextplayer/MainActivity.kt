package com.arcticoss.nextplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.arcticoss.nextplayer.navigation.MEDIA_ROUTE
import com.arcticoss.nextplayer.navigation.mediaNavGraph
import com.arcticoss.nextplayer.navigation.settingsNavGraph
import com.arcticoss.nextplayer.ui.theme.NextPlayerTheme
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NextPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = MEDIA_ROUTE
                    ) {
                        mediaNavGraph(context, navController)
                        settingsNavGraph(navController)
                    }
                }
            }
        }
    }
}

