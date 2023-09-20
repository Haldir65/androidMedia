package com.me.harris.awesomelib

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


fun callBroadCast(activity:Activity) {
    Log.e("-->", " >= 14")
    MediaScannerConnection.scanFile(
        activity, arrayOf(Environment.getExternalStorageDirectory().toString(),Environment.getExternalStorageDirectory().toString()+File.separator+Environment.DIRECTORY_MOVIES), null
    ) { path, uri ->

        /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
        Log.e("ExternalStorage", "Scanned $path:")
        Log.e("ExternalStorage", "-> uri=$uri")
    }
}

suspend fun scanExternalStorageDirRecursiveAndReturnFiles(activity: AppCompatActivity):List<String>{
    return if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val allFiles = withContext(Dispatchers.IO){
                val videoEnding = arrayOf("webm","mp4","mkv","avi")
                val allFiles = Environment.getExternalStorageDirectory().walkTopDown().filter {
                        f -> f.isFile && videoEnding.any { e -> f.path.endsWith(e) }
                }
                allFiles.toList()
            }
            allFiles.onEach {
                Log.w("=A=","${it.absolutePath}")
            }
            allFiles.map {file -> file.absolutePath }
    } else emptyList<String>()

}


