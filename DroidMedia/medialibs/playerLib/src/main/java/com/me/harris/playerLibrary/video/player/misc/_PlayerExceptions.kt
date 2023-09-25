package com.me.harris.playerLibrary.video.player.misc

import android.media.MediaCodec.CodecException

sealed class CodecExceptions:Exception(){


    class PrepareExtractorException:CodecExceptions()

}
