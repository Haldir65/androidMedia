package com.me.harris.action

internal interface PlayerCommand {
    companion object Numbers {
        const val START_PLAY = 1
        const val PREPARE = 2
        const val SET_SURFACE = 3
        const val PAUSE = 4
        const val STOP = 5
        const val RELEASE = 6
    }

}