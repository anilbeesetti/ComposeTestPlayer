package com.arcticoss.nextplayer.feature.media.video.composables

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arcticoss.nextplayer.feature.media.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowContentForMarshMellow(
    contentPadding: PaddingValues
) {
    val context = LocalContext.current
    RequestMultiplePermissions(permissions = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ), permissionGrantedContent = {
        ShowVideoFiles(
            contentPadding = contentPadding
        )
    }, permissionNotGrantedContent = { multiplePermissionsState ->
        ShowPermissionInfo(modifier = Modifier.padding(contentPadding),
            rationaleMessage = R.string.permission_info,
            actionButton = {
                if (multiplePermissionsState.shouldShowRationale) {
                    Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                        Text(
                            text = stringResource(id = R.string.grant_permission),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                } else {
                    Button(onClick = {
                        val uri = Uri.fromParts("package", context.packageName, null)
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri
                        )
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(id = R.string.open_settings),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        )
    })
}