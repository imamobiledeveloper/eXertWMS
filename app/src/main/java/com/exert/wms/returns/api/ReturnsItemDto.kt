package com.exert.wms.returns.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.transfer.api.TransferInSerialItemDto
import kotlinx.parcelize.Parcelize
import java.io.Serializable

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
data class PurchaseItemsDetailsDto1(
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


@Keep
data class
PurchaseReturnInvoiceRequestDto(
    val BranchID: Long = 0,
    val VendorID: Long = 0,
) {
    companion object
}

@Keep
data class
PurchaseReturnInvoiceListResponseDto(
    val success: Boolean,
    val PurchasesList: List<PurchaseDto>?
) {
    companion object
}

@Keep
data class
PurchaseDto(
    val PurchaseID: Long = 0,
    val PurchaseNumber: String = "",
) {
    companion object
}

@Keep
data class
PurchaseItemsListItemsRequestDto(
    val PurchaseID: Long = 0
) {
    companion object
}


@Keep
data class
PurchaseItemsListResponseDto(
    val success: Boolean,
    val Items: List<PurchaseItemsDetailsDto>?
) {
    companion object
}


@Parcelize
@Keep
data class PurchaseItemsDetailsDto(
    val WarehouseID: Long = 0,
    val Warehouse: String,
    val ItemID: Long = 0,
    val ItemCode: String,
    val ItemName: String,
    val ItemNameArabic: String,
    val Manfacturer: String,
    val UnitID: Int = 0,
    val UnitName: String,
    val Quantity: Double,
    val OrderedQty: Double,
    val BonusQuantity: Double,
    val SampleQuantity: Double,
    val SampleQty: Double,
    val ReturnedQty: Double,
    val BonusReturned: Double,
    val SampleReturned: Double,
    val BonusReceived: Double,
    val SampleReceived: Double,
    val Price: Double,
    val TotalCost: Double,
    val DiscountAmount: Double,
    val DiscountPercentage: Double,
    val ExchangeRate: Double,
    val NetTotal: Double,
    val Factor: Double,
    val ItemDiscountPercentage: Double,
    val ItemDiscount: Double,
    val VendorDiscountPercentage: Double,
    val VendorDiscount: Double,
    val TotalDiscount: Double,
    val LCYPrice: Double,
    val VATPercentage: Double,
    val VATAmount: Double,
    val CategoryCode: Double,
    val CategoryID: Double,
    val PurchaseItemID: Int,
    val TrackingTypes: Int,
    val IsSerialItem: Int,
    val UnitPrice: Double,
    val SQM: Double,
    val SerialItemList: List<PurchaseSerialItemListDto>?,
    var userReturningQty: Int = 0
) : Parcelable, Serializable {
    companion object

    fun getQuantityString() = Quantity.toString()

    fun getItemListName() = "$ItemCode - $ItemName"

    fun getPurchaseQtyString() = OrderedQty.toString()
    fun getUserReturningQtyString() = userReturningQty.toString()
}

@Parcelize
@Keep
data class PurchaseSerialItemListDto(
    val ItemSerialID: String? = null,
    val ItemID: String? = null,
    val SerialNumber: String = "0",
    val StockInHand: String? = null,
    val MFGDate: String? = null,
    val WarrantyDays: String? = null,
    val ActualQuantity: String? = null,
    val TrackingType: String? = null,
    val IsChecked: String? = null,
//    val SerialNumber: String? = "",
//    val ManufactureDate: String? = "",
//    val WarrantyPeriod: String? = "",
    val Quantity: Double? = 0.0,
) : Parcelable, Serializable {
    companion object

    //    fun getConvertedTransferInSerialItemDto()=  TransferInSerialItemDto(SerialNumber=SerialNumber?.takeIf { it.isNotEmpty() }?.let { it.toLong() } ?: 0, ManufactureDate = ManufactureDate, WarrantyPeriod =WarrantyPeriod ,Quantity= Quantity?.toInt()
//        ?: 0)
    fun getConvertedTransferInSerialItemDto() = TransferInSerialItemDto(
        SerialNumber = SerialNumber,
        ManufactureDate = MFGDate,
        WarrantyPeriod = WarrantyDays,
        Quantity = Quantity
    )

    fun getConvertedWarehouseSerialItemDetails() = WarehouseSerialItemDetails(
        WarehouseID = 0, SerialNumber = SerialNumber, MFGDate = MFGDate,
        WarentyDays = WarrantyDays, selected = false
    )
//    fun getConvertedWarehouseSerialItemDetails()= WarehouseSerialItemDetails(WarehouseID=0,SerialNumber=SerialNumber,MFGDate=ManufactureDate,
//        WarentyDays=WarrantyPeriod,selected = false)
}