package com.arcticoss.nextplayer.feature.media.video.composables

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.arcticoss.nextplayer.feature.media.R

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ShowContentForRedVelvet(
    contentPadding: PaddingValues,
) {
    val context = LocalContext.current
    RequestManageExternalStoragePermission(
        permissionGrantedContent = {
            ShowVideoFiles(
                contentPadding = contentPadding
            )
        },
        permissionNotGrantedContent = {
            ShowPermissionInfo(
                modifier = Modifier.padding(contentPadding),
                rationaleMessage = R.string.permission_info_red,
                actionButton = {
                    Button(
                        onClick = {
                            val uri = Uri.fromParts("package", context.packageName, null)
                            val intent = Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                uri
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
            )
        }

    )
}