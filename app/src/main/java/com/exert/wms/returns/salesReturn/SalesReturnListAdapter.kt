package com.exert.wms.returns.salesReturn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.ReturnSalesListItemLayoutBinding
import com.exert.wms.returns.api.SalesItemsDetailsDto

class SalesReturnListAdapter(
    private val itemsList: List<SalesItemsDetailsDto>,
    private val onItemTextClick: (SalesItemsDetailsDto) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolderBinding = ReturnSalesListItemLayoutBinding.inflate(
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
        private val holderBinding: ReturnSalesListItemLayoutBinding,
        private val onFeatureTextClick: (SalesItemsDetailsDto) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val stockItemListItemLayout: ConstraintLayout =
            holderBinding.stockItemListItemLayout

        fun bind(item: SalesItemsDetailsDto, position: Int) {
            holderBinding.whiteBg = position % 2 == 0
            holderBinding.setGreen = true

            holderBinding.soldCountTV.text = item.getSoldQtyString()
            holderBinding.returningCountTV.text = item.getUserReturningQtyString()
            holderBinding.itemNameTV.text = item.getItemListName()
            holderBinding.executePendingBindings()

            stockItemListItemLayout.setOnClickListener {
                onFeatureTextClick(item)
            }
        }
    }
}