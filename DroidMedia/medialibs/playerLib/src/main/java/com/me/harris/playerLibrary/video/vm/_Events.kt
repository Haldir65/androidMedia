package com.me.harris.playerLibrary.video.vm

import kotlinx.coroutines.flow.*

sealed class PlayState {
    data object PlayPlaying : PlayState()
    data object PlayPaused : PlayState()
    data object PlayIdle : PlayState()

}

sealed class MuteState {
    data object Mute:MuteState()
    data object NotMute:MuteState()
}

