package com.arcticoss.nextplayer.media.ui.mediascreen.composables

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.arcticoss.nextplayer.player.ui.playerscreen.composables.AddLifecycleEventObserver

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun RequestManageExternalStoragePermission(
    permissionGrantedContent: @Composable () -> Unit,
    permissionNotGrantedContent: @Composable () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(false)
    }
    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                hasPermission = Environment.isExternalStorageManager()
            }
            else -> {}
        }
    }
    if (hasPermission) {
        permissionGrantedContent()
    } else {
        permissionNotGrantedContent()
    }
}