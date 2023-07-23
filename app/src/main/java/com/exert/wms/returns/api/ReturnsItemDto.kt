package com.exert.wms.returns.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import kotlinx.parcelize.Parcelize

@Keep
data class PurchaseItemsDto(
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
data class PurchaseItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val WarehouseID: Long = 0,
    val ItemID: Long = 0,
    val ItemCode: String,
    val PurchaseQty: Double,
    val SalesQty: Double,
    val SerialItems: List<SerialItemsDto>,
) : Parcelable {
    companion object

    fun getItemIDString() = ItemID.toString()
    fun getPurchaseQtyString() = PurchaseQty.toString()
    fun getSalesQtyString() = SalesQty.toString()
}

@Keep
data class
PurchaseItemsRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<PurchaseItemsDetailsDto>
) {
    companion object
}

@Keep
data class SavePurchaseItemsResponse(
    val Success: Boolean,
    val SalesList: ArrayList<String> = arrayOf<String>().toCollection(ArrayList())
) {
    companion object
}

@Parcelize
@Keep
data class SalesItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val WarehouseID: Long = 0,
    val ItemID: Long = 0,
    val ItemCode: String,
    val PurchaseQty: Double,
    val SalesQty: Double,
    val SerialItems: List<SerialItemsDto>,
) : Parcelable {
    companion object

    fun getItemIDString() = ItemID.toString()
    fun getPurchaseQtyString() = PurchaseQty.toString()
    fun getSalesQtyString() = SalesQty.toString()
}

@Keep
data class
SalesItemsRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<SalesItemsDetailsDto>
) {
    companion object
}

@Keep
data class SaveSalesItemsResponse(
    val Success: Boolean,
    val SalesList: ArrayList<String> = arrayOf<String>().toCollection(ArrayList())
) {
    companion object
}