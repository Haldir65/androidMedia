package com.me.harris.simdjson

import android.os.Bundle
import android.os.FileUtils
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.simdjson.databinding.ActivitySimdJsonEntryBinding
import com.me.harris.simdjson.desc.TimeLine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.internal.ignoreIoExceptions
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.sql.Time
import kotlin.random.Random

class SimdJsonEntryActivity:AppCompatActivity() {

    private lateinit var binding:ActivitySimdJsonEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimdJsonEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.card1.setOnClickListener {
            // copy file
            val jsonStream = assets.open("twitter.json")
            val destDir = File(filesDir,"json")
            if (!destDir.exists()){
                destDir.mkdir()
            }
            val destPath = File(destDir,"twitter.json")
            if (!destPath.exists()){
                jsonStream.copyTo(destPath.outputStream())
            }
            require(destPath.exists())
            JsonJni.loadJsonFile(destPath.absolutePath)

            val start = SystemClock.elapsedRealtimeNanos()
            destPath.inputStream().use { stream ->
                val timeline = Json {
                    ignoreUnknownKeys = true
                }.decodeFromStream<TimeLine>(stream)
                val count = timeline.search_metadata.count
                val randomText = timeline.statuses[Random.nextInt(99)].text
                val now = SystemClock.elapsedRealtimeNanos()
                Log.i("=A=","【java】num of counts is ${count} ,time cost for parsing large json = ${(now - start)/1000} microseconds \n , randomtext = ${randomText}")
            }

        }

        binding.card2.setOnClickListener {
           Java11Support.showJava11Support()
        }
    }


}
