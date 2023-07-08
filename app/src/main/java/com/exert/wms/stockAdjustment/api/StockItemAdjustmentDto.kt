package com.exert.wms.stockAdjustment.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
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

@Parcelize
@Keep
data class StockItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val WarehouseID: Long = 0,
    val ItemID: Long=0,
    val ItemCode: String,
    val AdjustmentType: Int,
    val AdjustmentQty: Double,
    val SerialItems: List<SerialItemsDto>,
) : Parcelable {
    companion object

    fun getItemIDString() = ItemID.toString()
    fun getAdjustmentQtyString() = AdjustmentQty.toString()
}
//
//@Parcelize
//@Keep
//data class SerialItemsDto(
//    val SerialNumber: String? = "",
//    val ManufactureDate: String? = "",
//    val WarrantyPeriod: String? = "",
//    val Quantity: Long = 0,
//) : Serializable, Parcelable {
//    companion object
//}

@Keep
data class
StockAdjustmentRequestDto(
    val StockAdjustmentID: Long=0,
    val ItemsDetails: List<StockItemsDetailsDto>
) {
    companion object
}

//@Keep
//data class WarehouseListDto(
//    val success: Boolean,
//    val Warehouses: List<WarehouseDto>,
//) {
//    companion object
//}
//
//@Parcelize
//@Keep
//data class WarehouseDto(
//    val WarehouseID: Long,
//    val Warehouse: String,
//) : Serializable, Parcelable {
//    override fun toString(): String {
//        return this.Warehouse
//    }
//
//    companion object
//}