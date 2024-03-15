package com.me.harris.ui.screen

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.me.harris.composeworkmanager.R
import com.me.harris.logic.AudioLibEntry
import com.me.harris.logic.AvIfEntry
import com.me.harris.logic.BlurActivityEntry
import com.me.harris.logic.CameraPlayEntry
import com.me.harris.logic.ExtractFrameEntry
import com.me.harris.logic.FilterEntry
import com.me.harris.logic.GPUVideoEntry
import com.me.harris.logic.LibJpegEntry
import com.me.harris.logic.MediaInfoProbeEntry
import com.me.harris.logic.MediaKitEntry
import com.me.harris.logic.OpenGlEntry
import com.me.harris.logic.PickVideoEntry
import com.me.harris.logic.PngLibEntry
import com.me.harris.logic.SimdJsonEntry
import com.me.harris.logic.VideoClipEntry
import com.me.harris.logic.VideoPlayEntry
import com.me.harris.ui.widget.RoundedCardWithClick

@Composable
fun MediaMainScreen() {
    val layoutDirection = LocalLayoutDirection.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateEndPadding(layoutDirection)
            )
    ) {
        AluromaticScreenContent(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium)).fillMaxSize(),
        )
    }
}

@Composable
fun AluromaticScreenContent(modifier: Modifier) {
    val mContext = LocalContext.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        val largeBox = Modifier.padding(10.dp).align(alignment = Alignment.CenterHorizontally).padding(20.dp)
        RoundedCardWithClick(
            text = VideoPlayEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, VideoPlayEntry.targetClass))
        })
        RoundedCardWithClick(textModifier = largeBox ,text = PickVideoEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, PickVideoEntry.targetClass))
        })
        RoundedCardWithClick(text = CameraPlayEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, CameraPlayEntry.targetClass))
        })
        RoundedCardWithClick(text = MediaKitEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, MediaKitEntry.targetClass))
        })
        RoundedCardWithClick(text = FilterEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, FilterEntry.targetClass))
        })
        RoundedCardWithClick(text = OpenGlEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, OpenGlEntry.targetClass))
        })
        RoundedCardWithClick(text = VideoClipEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, VideoClipEntry.targetClass))
        })
        RoundedCardWithClick(text = ExtractFrameEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, ExtractFrameEntry.targetClass))
        })
        RoundedCardWithClick(text = GPUVideoEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, GPUVideoEntry.targetClass))
        })
        RoundedCardWithClick(text = PngLibEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, PngLibEntry.targetClass))
        })
        RoundedCardWithClick(text = LibJpegEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, LibJpegEntry.targetClass))
        })
        RoundedCardWithClick(text = AudioLibEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, AudioLibEntry.targetClass))
        })
        RoundedCardWithClick(text = MediaInfoProbeEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, MediaInfoProbeEntry.targetClass))
        })
        RoundedCardWithClick(text = AvIfEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, AvIfEntry.targetClass))
        })
        RoundedCardWithClick(text = SimdJsonEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, SimdJsonEntry.targetClass))
        })
        RoundedCardWithClick(text = BlurActivityEntry.text, onclick = {
            mContext.startActivity(Intent(mContext, BlurActivityEntry.targetClass))
        })
    }
}
