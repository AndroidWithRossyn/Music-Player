package com.yalantis.multiselection.lib

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent



class MultiSelectViewPager : ViewPager {

    
    var onClickCallback: (Float, Float) -> Boolean = { x, y -> false }

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    private var lastDownX: Float = -1f
    private var lastDownY: Float = -1f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastDownX = ev.x
                    lastDownY = ev.y
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("cmp", "$lastDownX")
                    return onClickCallback(lastDownX, lastDownY)
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }
}
