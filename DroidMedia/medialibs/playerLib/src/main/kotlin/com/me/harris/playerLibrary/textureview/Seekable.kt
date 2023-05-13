package com.me.harris.playerLibrary.textureview

interface Seekable
{

    fun seekTo(absTimeMillisecond:Long)
    fun forward(forwardMillisecond:Long)
    fun backWard(backwardMillisecond:Long)
}