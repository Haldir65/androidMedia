package com.me.harris.playerLibrary.video.player.datasource

import android.media.MediaDataSource
import android.util.Log
import java.io.RandomAccessFile

class LocalFileDataSource(val filepath:String): MediaDataSource() {

    private val rFile = RandomAccessFile(filepath,"r")


    override fun close() {
        rFile.close()
    }

    override fun readAt(position: Long, buffer: ByteArray?, offset: Int, size: Int): Int {
        val now = System.currentTimeMillis()
        rFile.seek(position)
        val len_read = rFile.read(buffer,offset,size)
        val cost = System.currentTimeMillis() - now
        if (cost>=2){
            Log.d("=A=","${Thread.currentThread().name} readAt position = ${position}  buffer size = ${buffer?.size} offset = ${offset} size = ${size} len_read = ${len_read} cost me ${cost}ms")
        }
        return len_read
    }

    override fun getSize(): Long {
       return rFile.length()
    }
}
