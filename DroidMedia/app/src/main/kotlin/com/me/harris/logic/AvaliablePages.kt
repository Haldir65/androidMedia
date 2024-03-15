package com.me.harris.logic

import com.daasuu.epf.VideoClipEntryActivity
import com.jadyn.mediakit.MediaKitEntryActivity
import com.me.harris.AwesomePickVideoActivity
import com.me.harris.audiolib.AudioLibEntryActivity
import com.me.harris.avif.AvIfEntryActivity
import com.me.harris.cameralib.CameraEntryActivity
import com.me.harris.composeworkmanager.BlurActivity
import com.me.harris.extractframe.ExtractFrameEntryActivity
import com.me.harris.filterlibrary.FilterEntryActivity
import com.me.harris.filterlibrary.opengl.enrty.OpenGlEntryActivity
import com.me.harris.gpuvideo.GPUVideoEntryActivity
import com.me.harris.libjpeg.ui.LibJpegEntryActivity
import com.me.harris.mediainfo.MediaInfoProbeActivity
import com.me.harris.playerLibrary.VideoPlayExtryActivity
import com.me.harris.pnglib.PngLibEntryActivity
import com.me.harris.simdjson.SimdJsonEntryActivity

sealed class AvaliablePages {

}

// don't make this an list of xxx
// we may customize some entry later
object  VideoPlayEntry{
    val text:String = "VideoPlayExtryActivity"
    val targetClass =  VideoPlayExtryActivity::class.java
}

object  CameraPlayEntry{
    val text:String = "CameraEntry"
    val targetClass =  CameraEntryActivity::class.java
}

object  MediaKitEntry{
    val text:String = "MediaKitEntryActivity"
    val targetClass =  MediaKitEntryActivity::class.java
}


object  FilterEntry{
    val text:String = "FilterEntryActivity"
    val targetClass =  FilterEntryActivity::class.java
}

object  OpenGlEntry {
    val text:String = "OpenGlEntryActivity"
    val targetClass =  OpenGlEntryActivity::class.java
}

object  VideoClipEntry {
    val text:String = "VideoClipEntryActivity"
    val targetClass =  VideoClipEntryActivity::class.java
}

object  ExtractFrameEntry {
    val text:String = "ExtractFrameEntryActivity"
    val targetClass =  ExtractFrameEntryActivity::class.java
}

object  GPUVideoEntry {
    val text:String = "GPUVideoEntryActivity"
    val targetClass =  GPUVideoEntryActivity::class.java
}

object  PngLibEntry {
    val text:String = "PngLibEntryActivity"
    val targetClass =  PngLibEntryActivity::class.java
}

object  LibJpegEntry {
    val text:String = "LibJpegEntryActivity"
    val targetClass =  LibJpegEntryActivity::class.java
}

object  AudioLibEntry{
    val text:String = "AudioLibEntryActivity"
    val targetClass =  AudioLibEntryActivity::class.java
}


object  PickVideoEntry{
    val text:String = "PickVideoActivity"
    val targetClass =  AwesomePickVideoActivity::class.java
}

object  MediaInfoProbeEntry{
    val text:String = "MediaInfoProbeActivity"
    val targetClass =  MediaInfoProbeActivity::class.java
}


object  AvIfEntry {
    val text:String = "AvIfEntryActivity"
    val targetClass =  AvIfEntryActivity::class.java
}

object  SimdJsonEntry {
    val text:String = "SimdJsonEntryActivity"
    val targetClass =  SimdJsonEntryActivity::class.java
}
object  BlurActivityEntry {
    val text:String = "BlurActivity"
    val targetClass =  BlurActivity::class.java
}



