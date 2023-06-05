package com.me.harris.serviceapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

const val KEY_VIDEO_URL = "extra_key_file_path"

interface PlayerLibService {

    fun startPlayVideoActivity(activity:AppCompatActivity,bundle:Bundle)

    suspend fun callSuspendFucnction()
}