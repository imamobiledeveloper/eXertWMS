package com.exert.wms.itemStocks.warehouse

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.ItemSerialNumbersListItemLayoutBinding
import com.exert.wms.itemStocks.api.ItemsDto

class SerialNumbersListAdapter(
    private val itemsList: List<ItemsDto>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolderBinding = ItemSerialNumbersListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WarehouseListViewHolder(viewHolderBinding)
    }

    override fun getItemCount(): Int {
        return itemsList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as WarehouseListViewHolder).bind(itemsList[position], position, mSelectedItem)
    }

    inner class WarehouseListViewHolder(
        holderBinding: ItemSerialNumbersListItemLayoutBinding
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val manufactureDate: TextView = holderBinding.manufactureDateTV
        private val warrantyPeriod: TextView = holderBinding.warrantyPeriodTV
        private val serialNumber: TextView = holderBinding.serialNumberTV

        fun bind(dto: ItemsDto, position: Int, selectedPosition: Int) {
            manufactureDate.text = "name".random().toString()
            warrantyPeriod.text = "120"
            serialNumber.text = "Hyderabad"
        }
    }
}