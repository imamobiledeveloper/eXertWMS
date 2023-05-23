package com.exert.wms.itemStocks.status

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.ItemStocksWarehouseListItemLayoutBinding
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseStockDetails

class WarehouseListAdapter(
    private val itemsList: List<WarehouseStockDetails>,
    private val onItemTextClick: (WarehouseStockDetails) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolderBinding = ItemStocksWarehouseListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WarehouseListViewHolder(viewHolderBinding, onItemTextClick)
    }

    override fun getItemCount(): Int {
        return itemsList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as WarehouseListViewHolder).bind(itemsList[position], position, mSelectedItem)
    }

    inner class WarehouseListViewHolder(
        holderBinding: ItemStocksWarehouseListItemLayoutBinding,
        private val onFeatureTextClick: (WarehouseStockDetails) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val warehouseName: TextView = holderBinding.warehouseNameTV
        private val stockCount: TextView = holderBinding.stockCountTV
        private val location: TextView = holderBinding.warehouseLocationTV
        private val warehouseItemLayout: ConstraintLayout = holderBinding.warehouseItemLayout

        fun bind(warehouse: WarehouseStockDetails, position: Int, selectedPosition: Int) {
            warehouseName.text = warehouse.WarehouseDescription
            stockCount.text = warehouse.FinalQuantity
            location.text = warehouse.LocationName
            warehouseItemLayout.setOnClickListener {
                onFeatureTextClick(warehouse)
            }
        }
    }
}