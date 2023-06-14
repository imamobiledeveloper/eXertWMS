package com.exert.wms.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


//class SpinnerCustomAdapter1(val context: Context, var list: List<WarehouseDto>,
//                           private val onItemTextClick: (String) -> Unit) : BaseAdapter() {
//
//    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val view: View
//        val holder: ItemHolder
//        if (convertView == null) {
//            view = inflater.inflate(R.layout.spinner_item_layout, parent, false)
//            holder = ItemHolder(view)
//            view?.tag = holder
//        } else {
//            view = convertView
//            holder = view.tag as ItemHolder
//        }
//        holder.textView.text = list.get(position).Warehouse
//
//        return view
//    }
//
//    override fun getItem(position: Int): Any {
//        return list[position];
//    }
//
//    override fun getCount(): Int {
//        return list.size;
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong();
//    }
//
//    private class ItemHolder(row: View?,private val onFeatureTextClick: (String) -> Unit?) {
//        val textView: TextView
//        init {
//            textView = row?.findViewById(R.id.textName) as TextView
//            textView.setOnClickListener {
//                onFeatureTextClick(itemName.text.toString())
//            }
//        }
//    }
//
//}

//class SpinnerCustomAdapter(
//    context: Context,
//    var list: List<WarehouseDto>,
//) :
//    ArrayAdapter<WarehouseDto?>(context, android.R.layout.simple_spinner_item, list) {
//
////    fun getView(position: Int, convertView: View,parent: ViewGroup): View? {
////        return initView(position, convertView, parent)
////    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return initView(position, convertView, parent)
//    }
//    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
////        return super.getDropDownView(position, convertView, parent)
//
//        var v: View? = null
//
//        if (position == 0) {
//            val tv = TextView(context)
//            tv.height = 0
//            tv.visibility = View.GONE
//            v = tv
//        } else {
////            v = super.getDropDownView(position, null, parent)
//            v = initView(position, convertView, parent)
//        }
//        parent.isVerticalScrollBarEnabled = false
////        return initView(position, convertView, parent)
//        return  v
//    }
////    fun getDropDownView1(
////        position: Int,
////        @Nullable convertView: View,
////        @NonNull parent: ViewGroup
////    ): View? {
////        return initView(position, convertView, parent)
////    }
//
//    private fun initView(
//        position: Int, convertView: View?,
//        parent: ViewGroup
//    ): View {
//        // It is used to set our custom view.
//        var convertView: View? = convertView
//        if (convertView == null) {
//            convertView =
//                LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false)
//        }
//        val textViewName = convertView!!.findViewById<TextView>(R.id.textName)
//        val currentItem: WarehouseDto? = getItem(position)
//
//        // It is used the name to the TextView when the current item is not null.
//
//        if (currentItem != null && textViewName!=null) {
//            textViewName.text = currentItem.toString()
//        }
//
////        if (position == 0) {
////            val tv = TextView(context)
////            tv.height = 0
////            tv.visibility = View.GONE
//////            v = tv
////        }else{
////            if (currentItem != null) {
////                textViewName.text = currentItem.toString()
////            }
////        }
////        else {
//////            v = super.getDropDownView(position, null, parent)
////            v = initView(position, convertView, parent)
////        }
//        return convertView
//    }
//}


//class SpinnerCustomAdapter(context: Context, items: List<WarehouseDto?>) :
//    ArrayAdapter<WarehouseDto?>(context, android.R.layout.simple_spinner_item, items) {
//    override fun getDropDownView(
//        position: Int,
//        convertView: View,
//        parent: ViewGroup
//    ): View {
//        return if (position == 0) {
//            initialSelection(true)
//        } else getCustomView(position, convertView, parent)
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return if (position == 0) {
//            initialSelection(false)
//        } else getCustomView(position, convertView, parent)
//    }
//
//    override fun getCount(): Int {
//        return super.getCount() + 1 // Adjust for initial selection item
//    }
//
//    private fun initialSelection(dropdown: Boolean): View {
//        // Just an example using a simple TextView. Create whatever default view
//        // to suit your needs, inflating a separate layout if it's cleaner.
//        val view = TextView(context)
//        view.text = "select"
////        val spacing = context.resources.getDimensionPixelSize(R.dimen.spacing_smaller)
////        view.setPadding(0, spacing, 0, spacing)
//        if (dropdown) { // Hidden when the dropdown is opened
//            view.height = 0
//        }
//        return view
//    }
//
//    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
//        // Distinguish "real" spinner items (that can be reused) from initial selection item
//        var position = position
//        val row =
//            if (convertView != null && convertView !is TextView) convertView else LayoutInflater.from(
//                context
//            ).inflate(com.exert.wms.R.layout.spinner_item_layout, parent, false)
//        position -= 1 // Adjust for initial selection item
//        val item: WarehouseDto? = getItem(position)
//
//        if (item != null) {
//            val tv = row.findViewById<TextView>(com.exert.wms.R.id.textName)
//            tv.text = item.toString()
//        }
//        // ... Resolve views & populate with data ...
//        return row
//    }
//}

class SpinnerCustomAdapter(context: Context, items: Array<String>,layoutResourceId:Int) :
    ArrayAdapter<String?>(context, layoutResourceId, items) {
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