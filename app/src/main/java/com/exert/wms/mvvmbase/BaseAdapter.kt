package com.exert.wms.mvvmbase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<BINDING : ViewDataBinding, T : ListAdapterItem>(var data: List<T>) :
    RecyclerView.Adapter<BaseViewHolder<BINDING>>() {

    @get:LayoutRes
    abstract val layoutId: Int
    abstract fun bind(binding: BINDING, item: T, position: Int)

    fun updateData(list: List<T>) {
        this.data = list
        notifyDataSetChanged()
    }

    fun getDataSize() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BINDING> {
        val binder = DataBindingUtil.inflate<BINDING>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
        return BaseViewHolder(binder)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BINDING>, position: Int) {
        bind(holder.binder, data[position], position)
    }

    override fun getItemCount(): Int = data.size
}