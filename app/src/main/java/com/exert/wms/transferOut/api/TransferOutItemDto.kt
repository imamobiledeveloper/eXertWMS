package com.exert.wms.transferOut.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import kotlinx.parcelize.Parcelize

@Keep
data class TransferOutDto(
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
data class TransferOutItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val WarehouseID: Long = 0,
    val ItemID: Long = 0,
    val ItemCode: String,
    val AdjustmentType: Int,
    val AdjustmentQty: Double,
    val SerialItems: List<SerialItemsDto>,
) : Parcelable {
    companion object

    fun getItemIDString() = ItemID.toString()
    fun getAdjustmentQtyString() = AdjustmentQty.toString()
}

@Keep
data class
TransferOutRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<TransferOutItemsDetailsDto>
) {
    companion object
}

@Keep
data class SaveTransferOutResponse(
    val Success: Boolean,
    val SalesList: ArrayList<String> = arrayOf<String>().toCollection(ArrayList())
) {
    companion object
}