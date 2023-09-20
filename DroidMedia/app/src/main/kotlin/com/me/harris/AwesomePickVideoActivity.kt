package com.me.harris

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.utils.VideoUtil
import com.me.harris.droidmedia.databinding.ActivityPickVideoBinding
import java.io.File

class AwesomePickVideoActivity:AppCompatActivity() {

    companion object {
        const val TAG = "=A="
    }

    private lateinit var binding:ActivityPickVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.card2.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            kotlin.runCatching {
                startActivityForResult(Intent.createChooser(intent,"选择播放文件"),2000)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2000 && resultCode == RESULT_OK){
            val path = com.me.harris.FileUtils.getImageAbsolutePath(this, data?.data).orEmpty()
            Log.e(TAG, "onActivityResult: url: ${data?.data}, path: $path")
            binding.textUrl.text = """
                =====
                ${path}
                =====
            """.trimIndent()
            val srcFile = File(path)
            if (srcFile.exists()){
                val saveDir = applicationContext.filesDir
                val name = srcFile.nameWithoutExtension
                val extension = srcFile.extension
                VideoUtil.setUrl(path)
                val dstFile = File("${saveDir}${File.separator}${name}_copy.${extension}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && false) {
                    val start = System.currentTimeMillis()
                    android.os.FileUtils.copy(srcFile.inputStream(),dstFile.outputStream()) // sendFile
                    val cost = System.currentTimeMillis() - start
                    require(dstFile.exists())
                    Log.w(TAG,"copy file ${srcFile.absolutePath} use send file to ${dstFile.absolutePath} cost me ${cost} millisecond")
                }
            }
        }
    }

}
