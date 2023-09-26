package com.exert.wms.delivery.deliveryNote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.StockAdjustmentItemNameListItemLayoutBinding
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto

class DeliveryNoteListAdapter(
    private val itemsList: List<DeliveryNoteItemsDetailsDto>,
    private val onItemTextClick: (DeliveryNoteItemsDetailsDto) -> Unit
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
        private val onFeatureTextClick: (DeliveryNoteItemsDetailsDto) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val stockItemListItemLayout: ConstraintLayout =
            holderBinding.stockItemListItemLayout

        fun bind(item: DeliveryNoteItemsDetailsDto, position: Int) {
            holderBinding.whiteBg = position % 2 == 0
            holderBinding.itemCountTV.text = item.getQuantityString()
            holderBinding.itemNameTV.text = item.getItemListName()
            holderBinding.executePendingBindings()

            stockItemListItemLayout.setOnClickListener {
                onFeatureTextClick(item)
            }
        }
    }
}