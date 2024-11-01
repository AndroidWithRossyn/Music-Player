package com.yalantis.multiselection.lib.adapter

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.io.Serializable


class ViewPagerAdapter(val pageWidth: Float) : PagerAdapter() {

    lateinit var pageLeft: View
    lateinit var pageRight: View

    override fun getPageWidth(position: Int): Float {
        return pageWidth 
    }

    override fun getCount(): Int = 2

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: View, position: Int): Any {
        val pager = container as ViewPager
        val view = getView(position, pager)
        pager.addView(view)
        view.tag = position
        return view
    }

    private fun getView(position: Int, pager: ViewPager): View = when (position) {
        0 -> pageLeft
        1 -> pageRight
        else -> throw IllegalStateException()
    }

}


