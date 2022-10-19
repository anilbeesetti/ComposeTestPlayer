package com.arcticoss.feature.media.presentation.composables

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle

private const val TAG = "RequestManageExternalSt"

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun RequestManageExternalStoragePermission(
    permissionGrantedContent: @Composable () -> Unit,
    permissionNotGrantedContent: @Composable () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember {
        mutableStateOf(Environment.isExternalStorageManager())
    }
    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            hasPermission = Environment.isExternalStorageManager()
        }
    }
    if (hasPermission) {
        permissionGrantedContent()
    } else {
        permissionNotGrantedContent()
    }
}