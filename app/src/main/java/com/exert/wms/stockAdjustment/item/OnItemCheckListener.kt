package com.exert.wms.stockAdjustment.item

import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails

interface OnItemCheckListener {
    fun onItemCheck(item: WarehouseSerialItemDetails)
    fun onItemUncheck(item: WarehouseSerialItemDetails)
}