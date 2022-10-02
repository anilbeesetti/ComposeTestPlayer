package com.arcticoss.nextplayer.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log

object PermissionUtils {

    private const val TAG = "PermissionUtils"

    fun hasPermission():Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "hasPermission: ${Environment.isExternalStorageManager()}")
            Environment.isExternalStorageManager()
        } else {
            TODO("VERSION.SDK_INT < R")
        }
    }

    fun grantPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val uri = Uri.fromParts("package", context.packageName, null)
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "grantPermission: Exception", e)
                context.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            }
        } else {
            TODO("VERSION.SDK_INT < R")
        }

    }
}