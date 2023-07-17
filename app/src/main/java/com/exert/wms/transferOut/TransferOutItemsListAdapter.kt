package com.exert.wms.transferOut

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.StockAdjustmentItemNameListItemLayoutBinding
import com.exert.wms.transferOut.api.TransferOutItemsDetailsDto

class TransferOutItemsListAdapter(
    private val itemsList: List<TransferOutItemsDetailsDto>,
    private val onItemTextClick: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolderBinding = StockAdjustmentItemNameListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemsListViewHolder(viewHolderBinding, onItemTextClick)
    }

    override fun getItemCount(): Int {
        return itemsList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemsListViewHolder).bind(itemsList[position], position, mSelectedItem)
    }

    inner class ItemsListViewHolder(
        private val holderBinding: StockAdjustmentItemNameListItemLayoutBinding,
        private val onFeatureTextClick: (String) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val itemName: TextView = holderBinding.itemNameTV

        fun bind(item: TransferOutItemsDetailsDto, position: Int, selectedPosition: Int) {
            holderBinding.whiteBg = position % 2 == 0
//            holderBinding.itemDto = item
            holderBinding.setGreen = item.AdjustmentType == 0

            holderBinding.itemCountTV.text = item.getAdjustmentQtyString()
            holderBinding.itemNameTV.text = item.getItemIDString()
            holderBinding.executePendingBindings()

            itemName.setOnClickListener {
                onFeatureTextClick(item.ItemCode)
            }
        }
    }
}