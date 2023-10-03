package com.me.harris.audiolib.audioPlayer.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


object PathTool {
    /** AssetFileDescriptor详解
     * 1、该对象提供了用于访问位于assets目录下的资源文件句柄AssetFileDescriptor，通过AssetFileDescriptor就可以用InputStream来进行访问文件了
     * 2、assets目录下的资源文件不会被编译成id，但是会直接copy到应用程序的指定目录中
     * 3、assets目录支持多级子目录
     * 4、必须要有应用的Context上下文和资源文件的文件名,如果在二级目录下则还要包含路径
     */
    fun getAssetFileDescriptor(context: Context, fileName: String?): AssetFileDescriptor? {
        try {
            return context.assets.openFd(fileName!!)
        } catch (io: IOException) {
            io.printStackTrace()
        }
        return null
    }

    /** 获取assets目录下的文件流句柄
     *
     */
    fun getInputStream(context: Context, fileName: String?): InputStream? {
        try {
            return context.assets.open(fileName!!)
        } catch (io: IOException) {
            io.printStackTrace()
        }
        return null
    }


    /** Uri位于android.net框架下
     * 1、它是统一资源描述符 由[scheme:]scheme-specific-part[#fragment]组成，既可以描述网络又可以描述本地
     * 2、提供了将字符串形式的地址转换成Uri方法
     */
    fun getUriByString(uriStr: String?): Uri? {
        return Uri.parse(uriStr)
    }

    /**
     * 复制assets文件到手机指定目录
     * Context:应用上下文
     * String srcPath:asserts目录下文件名
     * String sdPath:保存到手机目录的路径
     */
    fun copyAssetsToDst(context: Context, srcPath: String?, sdPath: String?) {
        try {
            val outFile = File(sdPath)
            if (outFile.exists()) {
                outFile.delete()
            }
            outFile.createNewFile()
            val `is` = context.assets.open(srcPath!!)
            val fos = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var byteCount: Int
            while (`is`.read(buffer).also { byteCount = it } != -1) {
                fos.write(buffer, 0, byteCount)
            }
           logd("finish")
            // 清空缓冲区并强制写入输出文件
            fos.flush()

            // 关闭文件句柄;当发生异常时是不会执行到这里来的
            `is`.close()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 安卓手机存储分为内部存储和外部存储
     * 1、内部存储;只有应用自己和超级用户才能访问，不同的设备产商可能这个路径不一样，但基本都在/data/xxx 目录下
     * 通过Context的getxxx系列函数得到,比如getFilesDir()等等;内部存储空间是有限制的，随着应用被删除一起删除
     * 2、外部存储;该存储可能是可移除的存储介质（例如 SD 卡）或内部（不可移除）存储。 保存到外部存储的文件所有应用都可以访问，没有大小限制，但是需要在
     * manifest申请读或者写权限读(READ_EXTERNAL_STORAGE) 或 写(WRITE_EXTERNAL_STORAGE),6.0以后还需要运行时再度申请权限
     *
     */
    // 为小米4的测试结果
    fun testPath(context: Context) {
        // /data/user/0/com.media
        val dataFile = context.dataDir
        logd("getDataDir() $dataFile")

        // ==== 内部存储 ===== //
        // /data/user/0/com.media/files
        val filesFile = context.filesDir
        logd("getFilesDir $filesFile")
        // /data/user/0/com.media/cache
        val cacheFile = context.cacheDir
        logd("getCacheDir $cacheFile")

        // /data/user/0/com.media/code_cache
        val shareFile = context.codeCacheDir
        logd("getCodeCacheDir $shareFile")

        // ==== 外部存储 ===== //

        // 获取外部存储状态;当处于MEDIA_MOUNTED时才是可以读写的，具体状态参考文档
        // /storage/emulated/0
        val state = Environment.getExternalStorageState()
        logd("getExternalStorageState $state")

        // 获取外部存储的根路径;不同手机厂商该路径有可能不一样
        val extDirFile = Environment.getExternalStorageDirectory()
        logd("getExternalStorageDirectory $extDirFile")

        // 外部存储默认公共目录；安卓系统默认提供了十个公共目录，存储在 外部存储根路径/xxx 目录下，比如Environment.DIRECTORY_DOWNLOADS对应
        // 外部存储根路径/Downloads，其它查看相关文档
        val pubFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        logd("getExternalStoragePublicDirectory $pubFile")

        // 外部存储私有目录；与应用的内部存储目录一样，会随着应用的卸载一并删除。可以被其它应用访问,它在 外部存储目录/Android/data应用包名/files/xxx目录名 下
        val privFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        logd("getExternalFilesDir $privFile")
        Environment.getRootDirectory()
    }

    fun logd(string: String) {
        Log.d("DDlogd", string)
    }
}