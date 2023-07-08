package com.exert.wms.stockReconciliation.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import kotlinx.parcelize.Parcelize

@Keep
data class StockItemReconciliationDto(
    val warehouse: String,
    val itemPartCode: String,
    val itemSerialNo: String,
    val itemNameEnglish: String,
    val itemNameArabic: String,
    val manufacturer: String,
    val systemStock: Long,
    val location: String,
    val quantity: Long,
) {
    companion object
}

@Parcelize
@Keep
data class ReconciliationItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val WarehouseID: Long = 0,
    val ItemID: Long = 0,
    val ItemCode: String,
    val location: String,
    val quantity: Double,
    val SerialItems: List<SerialItemsDto>,
) : Parcelable {
    companion object

    fun getItemIDString() = ItemID.toString()
//    fun getAdjustmentQtyString() = AdjustmentQty.toString()
}

@Keep
data class
StockReconciliationRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<ReconciliationItemsDetailsDto>
) {
    companion object
}