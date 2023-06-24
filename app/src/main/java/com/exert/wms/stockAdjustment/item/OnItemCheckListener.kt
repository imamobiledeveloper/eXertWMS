package com.exert.wms.stockAdjustment.item

import com.exert.wms.stockAdjustment.api.SerialItemsDto

interface OnItemCheckListener {
    fun onItemCheck(item: SerialItemsDto)
    fun onItemUncheck(item: SerialItemsDto)
}

interface OnItemAddListener {
    fun onAddItem(item: SerialItemsDto)
}