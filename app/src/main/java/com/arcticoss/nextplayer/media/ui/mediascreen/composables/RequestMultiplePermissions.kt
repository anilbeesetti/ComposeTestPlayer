package com.arcticoss.nextplayer.media.ui.mediascreen.composables

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.arcticoss.nextplayer.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun RequestMultiplePermissions(
    permissions: List<String>,
    permissionGrantedContent: @Composable () -> Unit,
    permissionNotGrantedContent: @Composable (MultiplePermissionsState) -> Unit
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)
    if (multiplePermissionsState.allPermissionsGranted) {
        permissionGrantedContent()
    } else {
        permissionNotGrantedContent(multiplePermissionsState)
    }
}