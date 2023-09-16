package com.exert.wms.transfer.api

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

@Keep
data class SaveTransferOutResponse(
    val Success: Boolean,
    val SalesList: ArrayList<String> = arrayOf<String>().toCollection(ArrayList())
) {
    companion object
}

@Parcelize
@Keep
data class TransferInItemsDetailsDto(
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
TransferInRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<TransferInItemsDetailsDto>
) {
    companion object
}

@Keep
data class SaveTransferInResponse(
    val Success: Boolean,
    val SalesList: ArrayList<String> = arrayOf<String>().toCollection(ArrayList())
) {
    companion object
}

@Parcelize
@Keep
data class TransferOutItemDetailsDto(
    val ItemSeqNumber: Int = 0,
    val ItemID: Long = 0,
    val ItemCode: String,
    val Quantity: Int,
    val SerialItems: List<SerialItemsDto>,
    val displayName:String
) : Parcelable {
    companion object

    fun getItemIDString() = ItemID.toString()
    fun getItemQuantityString() = Quantity.toString()
}

@Keep
data class TransferOutRequestDto(
    val TransferOutID: Int = 0,
    val FromWarehouseID: Long = 0,
    val ToWarehouseID: Long = 0,
    val ItemsDetails: List<TransferOutItemDetailsDto>,
) {
    companion object
}