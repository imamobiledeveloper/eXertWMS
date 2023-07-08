package com.exert.wms.stockReconciliation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.StockAdjustmentItemNameListItemLayoutBinding
import com.exert.wms.stockReconciliation.api.ReconciliationItemsDetailsDto

class StockReconciliationItemsListAdapter(
    private val itemsList: List<ReconciliationItemsDetailsDto>,
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

        fun bind(item: ReconciliationItemsDetailsDto, position: Int, selectedPosition: Int) {
            holderBinding.whiteBg = position % 2 == 0
            holderBinding.setGreen = true //item.adjustmentType == 0
//            holderBinding.itemDto = item
            holderBinding.executePendingBindings()

            itemName.setOnClickListener {
                onFeatureTextClick(item.ItemCode)
            }
        }
    }
}