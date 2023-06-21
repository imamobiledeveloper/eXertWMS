package com.exert.wms.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerCustomAdapter(context: Context, items: Array<String>, layoutResourceId: Int) :
    ArrayAdapter<String>(context, layoutResourceId, items) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        val view: View?
        if (position == 0) {
            val tv = TextView(context)
            tv.height = 0
            tv.visibility = View.GONE
            view = tv
        } else {
            view = super.getDropDownView(position, null, parent)
        }
        return view
    }
}