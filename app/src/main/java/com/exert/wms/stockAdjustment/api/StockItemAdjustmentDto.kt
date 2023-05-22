package com.exert.wms.stockAdjustment.api

import androidx.annotation.Keep

@Keep
data class StockItemAdjustmentDto(
    val warehouse: String,
    val itemPartCode: String,
    val itemSerialNo: String,
    val itemNameEnglish: String,
    val itemNameArabic: String,
    val manufacturer: String,
    val systemStock: Long,
    val adjustmentType: String,
    val adjustmentQuantity: Long,
    val cost: Double,
    val adjustmentTotalCost: Double,
) {
    companion object
}

@Keep
data class
StockAdjustmentRequestDto( val itemPartCode: String){
    companion object
}