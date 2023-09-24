package com.exert.wms.transfer.transferIn.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.StockAdjustmentItemNameListItemLayoutBinding
import com.exert.wms.transfer.api.ExternalTransferItemsDto

class TransferInItemsListAdapter(
    private val itemsList: List<ExternalTransferItemsDto>,
    private val onItemTextClick: (ExternalTransferItemsDto) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        (holder as ItemsListViewHolder).bind(itemsList[position], position)
    }

    inner class ItemsListViewHolder(
        private val holderBinding: StockAdjustmentItemNameListItemLayoutBinding,
        private val onFeatureTextClick: (ExternalTransferItemsDto) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val layout: ConstraintLayout = holderBinding.stockItemListItemLayout

        fun bind(item: ExternalTransferItemsDto, position: Int) {
            holderBinding.rightArrowIV.visibility = View.VISIBLE
            holderBinding.whiteBg = position % 2 == 0

            holderBinding.itemCountTV.setTextColor(Color.BLACK)
            holderBinding.itemCountTV.text = item.getQuantityString()
            holderBinding.itemNameTV.text = item.getItemListName()
            holderBinding.executePendingBindings()

            layout.setOnClickListener {
                onFeatureTextClick(item)
            }
        }
    }
}