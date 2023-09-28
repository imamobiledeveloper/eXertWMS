package com.exert.wms.delivery.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import com.exert.wms.transfer.api.TransferInSerialItemDto
import com.exert.wms.transfer.api.TransferSerialItemListDto
import kotlinx.parcelize.Parcelize

@Keep
data class DeliveryReceiptItemsDto(
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
data class DeliveryReceiptItemsDetailsDto(
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
DeliveryReceiptItemsRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<DeliveryReceiptItemsDetailsDto>
) {
    companion object
}

@Keep
data class SaveDeliveryReceiptItemsResponse(
    val Success: Boolean,
    val SalesList: ArrayList<String> = arrayOf<String>().toCollection(ArrayList())
) {
    companion object
}

@Parcelize
@Keep
data class DeliveryNoteItemsDetailsDto(
    val WarehouseID: Long = 0,
    val Warehouse: String,
    val ItemID: Long = 0,
    val ItemCode: String,
    val ItemName: String,
    val ItemNameArabic: String,
    val Manfacturer: String,
    val UnitID: Int = 0,
    val UnitName: String,
    val QTYReceived: Double,
    val QTYReceiving: Double,
    val QTYBackOrder: Double,
    val QtyBalance: Double,
    val QtyCanceled: Double,
    val Quantity: Double,
    val QTYOrdered: Double,
    val Price: Double,
    val DiscountAmount: Double,
    val DiscountPercentage: Double,
    val AllowBackOrder: Boolean,
    val NetTotal: Double,
    val Factor: Double,
    val TotalCost: Double,
    val SalesOrderID: Int,
    val SalesOrderItemID: Int,
    val ItemDiscountPercentage: Double,
    val ItemDiscount: Double,
    val TrackingTypes: Int,
    val IsSerialItem: Int,
    val CC: String,
    val VATPercentage: Double,
    val SQM: Double,
    val SerialItems: List<TransferSerialItemListDto>?,
) : Parcelable, java.io.Serializable {
    companion object

    fun getQuantityString() = Quantity.toString()

    fun getItemListName() = "$ItemCode - $ItemName"
}

@Keep
data class
DeliveryNoteItemsResponseDto(
    val success: Boolean,
    val Items: List<DeliveryNoteItemsDetailsDto>?
) {
    companion object
}

@Keep
data class
DeliveryNoteItemsListWithOutItemsRequestDto(
    val BranchID: Long = 0,
    val CustomerID: Int = 0,
    val SalesOrderIDs: List<SalesOrderIDDto>,
//    val ItemsDetails: List<DeliveryNoteItemDto>
) {
    companion object
}

@Keep
data class
DeliveryNoteItemsListRequestDto(
    val BranchID: Long = 0,
    val CustomerID: Int = 0,
    val SalesOrderIDs: List<SalesOrderIDDto>,
    val ItemsDetails: List<DeliveryNoteItemDto>
) {
    companion object
}

@Keep
data class
SalesOrderIDDto(
    val SalesOrderID: Long = 0
) {
    companion object
}

@Keep
data class
DeliveryNoteItemDto(
    val ItemSeqNumber: Int = 0,
    val ItemID: Long = 0,
    val WarehouseID: Long = 0,
    val UnitID: Int = 0,
    val SalesOrderItemID: Int = 0,
    val TrackingTypes: Int = 0,
    val Quantity: Double = 0.0,
    val SerialItems: List<TransferInSerialItemDto>
) {
    companion object
}

@Keep
data class SaveDeliveryNoteItemsResponse(
    val Success: Boolean,
    val DeliveryNoteID: String,
    val ErrorMessage: String
) {
    companion object
}

@Keep
data class
SalesOrdersRequestDto(
    val BranchID: Long = 0,
    val CustomerID: Long = 0,
) {
    companion object
}

@Keep
data class
SalesOrdersListResponseDto(
    val success: Boolean,
    val SalesOrders: List<SalesOrdersDto>?
) {
    companion object
}

@Keep
data class
SalesOrdersDto(
    val SalesOrderID: Long = 0,
    val SalesOrderNumber: String
) {
    companion object
}

