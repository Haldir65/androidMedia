package com.me.harris.extractframe.parallel

import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
import android.os.Build
import android.util.Log
import androidx.annotation.WorkerThread
import com.me.harris.extractframe.contract.ExtractConfiguration
import kotlinx.coroutines.withContext
import okio.Okio
import okio.Source
import okio.sink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.measureTimeMillis

const val PARALLISM = ExtractConfiguration.EXTRACT_FRAME_PARALLEISM // todo



internal fun getVideoDurationInMicroSeconds (filepath:String):Long {
    if (!filepath.startsWith("http"))require(File(filepath).exists())
    val extractor = MediaExtractor()
    extractor.setDataSource(filepath)
    for (i in 0 until extractor.trackCount){
        val format = extractor.getTrackFormat(i)
        val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()
        if (mime.startsWith("video/")){
            val keyFrameRate = runCatching { format.getInteger(MediaFormat.KEY_FRAME_RATE) }.getOrElse { -1 } // may throw
            val duration = format.getLong(MediaFormat.KEY_DURATION) // microseconds
            val width = format.getInteger(MediaFormat.KEY_WIDTH)
            val height = format.getInteger(MediaFormat.KEY_HEIGHT)
            return duration
        }
    }
    error("unpxpected!")
}

internal data class Range(val points:List<Long>)

internal class ExtractMouse(val id:Int,val filepath:String,val saveDirPath:String,val range:Range) {



    @WorkerThread
    suspend fun doExtractAndSaveToDir(){
        val a = MediaMetadataRetriever()
        a.setDataSource(filepath)
        range.points.onEach { absTime ->
            Log.i("=A=","""

                第${id}个worker 抽${absTime/1000_000}秒的图片
                线程信息【Thread】 ${Thread.currentThread().id} ${Thread.currentThread().name}

            """.trimIndent())
            val start = System.currentTimeMillis() // MediaMetadataRetriever.OPTION_CLOSEST 要解码，超级慢
            val bmp = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O_MR1) {
                a.getScaledFrameAtTime(absTime,MediaMetadataRetriever.OPTION_PREVIOUS_SYNC,1920/4,1080/4)
            }else {
                a.getFrameAtTime(absTime,OPTION_PREVIOUS_SYNC)
            }
            val c1 = System.currentTimeMillis() - start
            bmp?.let { mp ->
                val cost = measureTimeMillis {
                    savePicFile(bitmap = mp, savePath = "${saveDirPath}${File.separator}${absTime}.jpg")
                }
                Log.d("=A=","""

                    【Thread】${Thread.currentThread().name} ${Thread.currentThread().id}
                    extract frame at ${absTime} cost
                     ${c1} milliseconds
                    save bitmap to file ${saveDirPath}${File.separator}${absTime}.jpg cost me
                    $cost milliseconds

                """.lineSequence().joinToString(transform = String::trimStart, separator = System.lineSeparator()))
            }
        }

    //【Thread】DefaultDispatcher-worker-2 2758
    //extract frame at 145000000 cost
    //559 milliseconds
    //save bitmap to file /data/user/0/com.me.harris.droidmedia/files/photos3/145000000.jpg cost me
    //6 milliseconds
    }
}

internal fun distributingTasksToIndividualMouse(filepath:String,saveDir:String):List<ExtractMouse>{
    // 每隔10s 抽一张图
    val gap = ExtractConfiguration.EXTRACT_FRAME_GAP_IN_BETWEEN_SECONDS*1000_000
    File(saveDir).deleteRecursively()
    if (!File(saveDir).exists()) File(saveDir).mkdirs()
    val durationUs = getVideoDurationInMicroSeconds(filepath)
    val allPoints = List((durationUs/gap).toInt()){ a ->
        a * gap.toLong()
    }
    val chunks = allPoints.chunked((allPoints.size/PARALLISM).coerceAtLeast(1))

    val result = chunks.mapIndexed { index, c  -> ExtractMouse(id = index+1, filepath = filepath, saveDirPath = saveDir, range = Range(c)) }
    val str = """
        【Thread】 ${Thread.currentThread().id} ${Thread.currentThread().name}
        distributing result is
        一共${PARALLISM}个分片
        ${result.joinToString(separator = System.lineSeparator()) { a -> a.range.points.joinToString(separator = ",") { c -> "${(c/1000_000).toString()}秒" } }}
    """.lineSequence().joinToString(transform = String::trimStart, separator = System.lineSeparator())
    Log.w("=A=",str)
    return result
}

@Throws(IOException::class)
private fun savePicFile(bitmap: Bitmap, savePath: String) {
    val file = File(savePath)
    if (!file.exists()) {
        file.createNewFile()
    }
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
}

internal fun saveBitMapToDir(bitmap:Bitmap,dir:String,fileName:String){
    val file = File("${dir}${File.separator}${fileName}")
    if (!file.exists()) {
        file.createNewFile()
    }
}
