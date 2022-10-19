package com.arcticoss.feature.media.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

private const val TAG = "RequestMultiplePermissi"

@ExperimentalPermissionsApi
@Composable
fun RequestMultiplePermissions(
    permissions: List<String>,
    permissionGrantedContent: @Composable () -> Unit,
    permissionNotGrantedContent: @Composable (MultiplePermissionsState) -> Unit
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
    if (multiplePermissionsState.allPermissionsGranted) {
        permissionGrantedContent()
    } else {
        permissionNotGrantedContent(multiplePermissionsState)
    }
}