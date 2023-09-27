package com.me.harris.playerLibrary.video.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout


internal fun interface OnDoubleClickListener{

    fun onDoubleClick(x:Float,y:Float)
}
class DoubleClickDetectingConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    style: Int = 0,
) : ConstraintLayout(context, attrs, style) {



    private val listener = GestureListener()

    private val detector = GestureDetector(context, listener)

    internal fun setOnDoubleClickListener(l:OnDoubleClickListener){
        listener.onDoubleClickListener = l
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event)
    }


}

private class GestureListener : GestureDetector.SimpleOnGestureListener() {
    var onDoubleClickListener:(OnDoubleClickListener)? = null

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleClickListener?.onDoubleClick(e.x,e.y)
        Log.d("Double Tap", "Tapped at: (" + e.x + "," + e.y + ")");
        return true
    }
}



