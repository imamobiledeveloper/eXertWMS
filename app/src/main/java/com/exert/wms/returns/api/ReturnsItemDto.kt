package com.exert.wms.returns.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class
PurchaseItemsRequestDto(
    val BranchID: Long = 0,
    val VendorID: Long = 0,
    val PurchaseID: Long = 0,
    val ItemsDetails: List<PurchaseSaveItemsDetailsDto>
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
    val ItemSeqNumber: Int = 0,
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
    val ReturnedQty: Double = 0.0,
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
    val CategoryID: Int,
    val PurchaseItemID: Int,
    val TrackingTypes: Int,
    val IsSerialItem: Int,
    val UnitPrice: Double,
    val SQM: Double,
    var userReturningQty: Double = 0.0,
    val SerialItems: List<SerialItemsDto>? = emptyList(),
) : Parcelable, Serializable {
    companion object

    fun getItemListName() = "$ItemCode - $ItemName"

    fun getPurchaseQtyString() = OrderedQty.toString()
    fun getUserReturningQtyString() = userReturningQty.toString()
}

@Parcelize
@Keep
data class PurchaseSaveItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val ItemID: Long = 0,
    val WarehouseID: Long = 0,
    val UnitID: Int = 0,
    val CategoryID: Int,
    val Quantity: Double,
    val OrderedQty: Double,
    val Price: Double,
    val DiscountAmount: Double,
    val DiscountPercentage: Double,
    val Factor: Double,
    val ExchangeRate: Double,
    val ItemDiscountPercentage: Double,
    val ItemDiscount: Double,
    val VendorDiscountPercentage: Double,
    val VendorDiscount: Double,
    val UnitPrice: Double,
    val LCYPrice: Double,
    val VATPercentage: Double,
    val VATAmount: Double,
    val PurchaseItemID: Int,
    val TrackingTypes: Int,
    val SerialItems: List<SerialItemsDto>? = emptyList()

) : Parcelable, Serializable {
    companion object
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
    val Quantity: Double? = 0.0,
) : Parcelable, Serializable {
    companion object
}

@Keep
data class SavePurchaseItemsResponse(
    val Success: Boolean,
    val PurchaseReturnID: String,
    val ErrorMessage: String
) {
    companion object
}

@Parcelize
@Keep
data class SalesItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
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
    val BonusQuantity: Double,
    val InvoicedQty: Double,
    val ReturnedQty: Double = 0.0,
    val BonusInvoiced: Double,
    val BonusReturned: Double,
    val SampleQty: Double,
    val Price: Double,
    val SalesPrice: Double,
    val TotalCost: Double,
    val DiscountAmount: Double,
    val DiscountPercentage: Double,
    val ItemDiscountPercentage: Double,
    val ItemDiscount: Double,
    val TotalDiscount: Double,
    val NetTotal: Double,
    val VATPercentage: Double,
    val VATAmount: Double,
    val TotalAverageCost: Double,
    val BonusPercentageQuantity: Double,
    val TrackingTypes: Int,
    val Factor: Double,
    val ExchangeRate: Double,
    val LCYPrice: Double,
    val UnitPrice: Double,
    val CategoryCode: Double,
    val CategoryID: Int,
    val Packing: Double,
    val SalesItemID: Int,
    val IsSerialItem: Int,
    val SerialItems: List<SerialItemsDto>? = emptyList(),
    var userReturningQty: Double = 0.0,

    ) : Parcelable, Serializable {
    companion object

    fun getItemListName() = "$ItemCode - $ItemName"

    fun getSoldQtyString() = InvoicedQty.toString()
    fun getUserReturningQtyString() = userReturningQty.toString()
}

@Keep
data class
SalesReturnInvoiceRequestDto(
    val BranchID: Long = 0,
    val CustomerID: Long = 0,
) {
    companion object
}


@Keep
data class
SalesReturnInvoiceListResponseDto(
    val success: Boolean,
    val SalesList: List<SalesDto>?
) {
    companion object
}

@Keep
data class
SalesDto(
    val SalesID: Long = 0,
    val SalesNumber: String = "",
) {
    companion object
}

@Keep
data class
SalesItemsListItemsRequestDto(
    val SalesID: Long = 0
) {
    companion object
}

@Keep
data class
SalesItemsListResponseDto(
    val success: Boolean,
    val Items: List<SalesItemsDetailsDto>?
) {
    companion object
}

@Parcelize
@Keep
data class SalesSaveItemsDetailsDto(
    val ItemSeqNumber: Int = 0,
    val ItemID: Long = 0,
    val WarehouseID: Long = 0,
    val UnitID: Int = 0,
    val CategoryID: Int,
    val Quantity: Double,
    val Price: Double,
    val Factor: Double,
    val ExchangeRate: Double,
    val LCYPrice: Double,
    val DiscountAmount: Double,
    val DiscountPercentage: Double,
    val BonusPercentageQuantity: Double,
    val ItemDiscountPercentage: Double,
    val ItemDiscount: Double,
    val UnitPrice: Double,
    val VATPercentage: Double,
    val VATAmount: Double,
    val SalesItemID: Int,
    val TrackingTypes: Int,
    val SerialItems: List<SerialItemsDto>? = emptyList()

) : Parcelable, Serializable {
    companion object
}


@Keep
data class
SalesItemsRequestDto(
    val BranchID: Long = 0,
    val CustomerID: Long = 0,
    val SalesID: Long = 0,
    val ItemsDetails: List<SalesSaveItemsDetailsDto>
) {
    companion object
}

@Keep
data class SaveSalesItemsResponse(
    val Success: Boolean,
    val SalesReturnID: String,
    val ErrorMessage: String
) {
    companion object
}