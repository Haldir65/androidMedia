@file:JvmName("frmes")
package com.me.harris.extractframe.finale

import com.me.harris.extractframe.finale.creator.ExtractConfig

internal data class FrameStates(val status:ExtractStatus) {

}

internal sealed class ExtractStatus{
    data object IDLE: ExtractStatus()
    data object STARTED: ExtractStatus()
    data object COMPLETED: ExtractStatus()
}



internal sealed class Event(){
    data object ExtractStartEvent:Event()
    data class ExtractDoneEvent(val costMilliseconds:Long,val config: ExtractConfig):Event()
    data object ExtractFailedEvent:Event()
    class ExtractedOneFrame(val timeMicroSeconds:Long):Event()
}
