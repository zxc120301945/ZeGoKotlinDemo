package com.kotlin.demo.two.widget

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Mark on 2016/3/15
 *
 * Des: 列表的分隔线.
 */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        if (parent.getChildPosition(view) != 0)
            outRect.top = space
    }
}