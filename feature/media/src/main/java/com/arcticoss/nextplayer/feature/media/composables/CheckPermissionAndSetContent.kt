package com.arcticoss.nextplayer.feature.media.composables

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import com.arcticoss.nextplayer.core.ui.AddLifecycleEventObserver
import com.arcticoss.nextplayer.feature.media.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CheckPermissionAndSetContent(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val permissions = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var hasPermission by remember { mutableStateOf(false) }
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)

    AddLifecycleEventObserver(lifecycleOwner = lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_START) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    hasPermission = Environment.isExternalStorageManager()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !hasPermission) {
            ShowPermissionInfo(
                modifier = Modifier.padding(innerPadding),
                rationaleMessage = R.string.permission_info_red,
                actionButton = {
                    IconTextButton(
                        title = stringResource(id = R.string.open_settings),
                        icon = Icons.Rounded.Settings,
                        onClick = { context.launchApplicationAllFilesAccessPermission() }
                    )
                }
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
            !multiplePermissionsState.allPermissionsGranted
        ) {
            ShowPermissionInfo(modifier = Modifier.padding(innerPadding),
                rationaleMessage = R.string.permission_info,
                actionButton = {
                    if (multiplePermissionsState.shouldShowRationale) {
                        IconTextButton(
                            title = stringResource(id = R.string.grant_permission),
                            onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }
                        )
                    } else {
                        IconTextButton(
                            title = stringResource(id = R.string.open_settings),
                            icon = Icons.Rounded.Settings,
                            onClick = { context.launchApplicationDetailSettings() }
                        )
                    }
                }
            )
        }

        val permissionGranted = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && hasPermission) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && multiplePermissionsState.allPermissionsGranted) ||
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        if (permissionGranted) {
            content(innerPadding)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun Context.launchApplicationAllFilesAccessPermission() {
    val intent = Intent(
        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}


fun Context.launchApplicationDetailSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}
