package com.exert.wms.returns.salesReturn

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.ReturnPurchaseSaleListItemLayoutBinding
import com.exert.wms.returns.api.SalesItemsDetailsDto

class SalesReturnListAdapter(
    private val itemsList: List<SalesItemsDetailsDto>,
    private val onItemTextClick: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolderBinding = ReturnPurchaseSaleListItemLayoutBinding.inflate(
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
        private val holderBinding: ReturnPurchaseSaleListItemLayoutBinding,
        private val onFeatureTextClick: (String) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val itemName: TextView = holderBinding.itemNameTV

        fun bind(item: SalesItemsDetailsDto, position: Int) {
            holderBinding.whiteBg = position % 2 == 0
//            holderBinding.itemDto = item
            holderBinding.setGreen = true //item.AdjustmentType == 0

            holderBinding.purchaseCountTV.text = item.getPurchaseQtyString()
            holderBinding.returningCountTV.text = item.getSalesQtyString()
            holderBinding.itemNameTV.text = item.getItemIDString()
            holderBinding.executePendingBindings()

            itemName.setOnClickListener {
                onFeatureTextClick(item.ItemCode)
            }
        }
    }
}