package com.exert.wms.itemStocks.warehouse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.ItemSerialNumbersListItemLayoutBinding
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails

class SerialNumbersListAdapter(
    private val itemsList: List<WarehouseSerialItemDetails>,
    private val checkBoxState: Boolean
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
        (holder as WarehouseListViewHolder).bind(
            itemsList[position],
            position,
            mSelectedItem,
            checkBoxState
        )
    }

    inner class WarehouseListViewHolder(
        holderBinding: ItemSerialNumbersListItemLayoutBinding
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val manufactureDate: TextView = holderBinding.manufactureDateTV
        private val warrantyPeriod: TextView = holderBinding.warrantyPeriodTV
        private val serialNumber: TextView = holderBinding.serialNumberTV
        private val checkBox: CheckBox = holderBinding.serialNoCheckBox

        fun bind(serialItem: WarehouseSerialItemDetails, position: Int, selectedPosition: Int, checkBoxState: Boolean) {
            manufactureDate.text = serialItem.MFGDate
            warrantyPeriod.text = serialItem.WarentyDays
            serialNumber.text = serialItem.SerialNumber
            checkBox.visibility = if (checkBoxState) View.VISIBLE else View.GONE
            checkBox.isChecked= serialItem.selected
            checkBox.setOnCheckedChangeListener { compoundButton, checked ->
                serialItem.selected=checked
            }
        }
    }
}