package com.me.harris.awesomelib.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.BuildConfig

object StoragePermissSucks {

    @RequiresApi(Build.VERSION_CODES.R)
    fun grantManageExternalStoragePermission(activity:AppCompatActivity){
        val uri = Uri.parse("package:${BuildConfig.LIBRARY_PACKAGE_NAME}")

        activity.startActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                uri
            )
        )
    }
}