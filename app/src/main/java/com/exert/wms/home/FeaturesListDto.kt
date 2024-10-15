package com.exert.wms.home

import androidx.annotation.Keep
import com.exert.wms.R


@Keep
class FeaturesListDto {
    companion object

    fun getFeaturesList(): List<FeatureDto> {
        val list = ArrayList<FeatureDto>()
        list.add(FeatureDto("Item Stocks", R.drawable.ic_item_stocks))
        list.add(FeatureDto("Stock Adjustment", R.drawable.ic_stock_adjustment))
//        list.add(FeatureDto("Stock Reconciliation", R.drawable.ic_stock_reconcilication))
        list.add(FeatureDto("Transfer Out", R.drawable.ic_transfer_out))
        list.add(FeatureDto("Transfer In", R.drawable.ic_transfer_in))
        list.add(FeatureDto("Goods Received", R.drawable.ic_delivery_receipt))
        list.add(FeatureDto("Material Delivery", R.drawable.ic_delivery_note))
        list.add(FeatureDto("Purchase Return", R.drawable.ic_purchase_return))
        list.add(FeatureDto("Sales Return", R.drawable.ic_sales_return))
        list.add(FeatureDto("Logout", R.drawable.ic_logout))
        return list.toList()
    }
}

@Keep
data class FeatureDto(
    val name: String,
    val drawableId: Int
) {
    companion object
}