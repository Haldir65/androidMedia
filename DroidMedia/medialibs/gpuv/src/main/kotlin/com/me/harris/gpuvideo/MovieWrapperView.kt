package com.me.harris.gpuvideo

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Aspect 16 : 9 of View
 */
class MovieWrapperView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    style: Int = 0,
): FrameLayout(context, attrs, style) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        setMeasuredDimension(measuredWidth, measuredWidth / 16 * 9)
    }
}