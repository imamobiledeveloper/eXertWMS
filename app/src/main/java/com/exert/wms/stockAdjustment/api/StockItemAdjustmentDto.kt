package com.exert.wms.stockAdjustment.api

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

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
StockAdjustmentRequestDto(val itemPartCode: String) {
    companion object
}

@Keep
data class WarehouseListDto(
    val success: Boolean,
    val Warehouses: List<WarehouseDto>,
) {
    companion object
}
@Parcelize
@Keep
data class WarehouseDto(
    val WarehouseID: Long,
    val Warehouse: String,
) : Serializable,Parcelable {
    override fun toString(): String {
        return this.Warehouse
    }
    companion object
}