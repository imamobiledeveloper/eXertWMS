package com.exert.wms.transfer.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.SerialItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import kotlinx.parcelize.Parcelize
import java.io.Serializable

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
    val TransferOutID: Long,
    val ErrorMessage: String="",
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

@Parcelize
@Keep
data class TransferInItemsResponseDto(
    val success: Boolean,
    val ExternalTransferDetails: List<ExternalTransferDetailsDto>?,
//    val WarehouseID: Long = 0,
//    val ItemID: Long = 0,
//    val ItemCode: String,
//    val AdjustmentType: Int,
//    val AdjustmentQty: Double,
//    val SerialItems: List<SerialItemsDto>,
) : Parcelable {
    companion object

//    fun getItemIDString() = ItemID.toString()
//    fun getAdjustmentQtyString() = AdjustmentQty.toString()
}

@Parcelize
@Keep
data class
ExternalTransferDetailsDto(
    val FromBranchID: Long = 0,
    val ToBranchID: Long = 0,
    val Remarks: String?,
    val TruckNumber: String?,
    val DriverName: String?,
    val TransporterName: String?,
    val ItemList: List<ExternalTransferItemsDto>?
) : Parcelable {
    companion object
}

@Parcelize
@Keep
data class
ExternalTransferItemsDto(
    val TransferOutItemID: Long = 0,
    val ExternalTransferItemID: Long = 0,
    val ExternalReceivableItemID: Long = 0,
    val Warehouse: String? = "",
    val FromWarehouse: String? = "",
    val ToWarehouse: String? = "",
    val ItemID: Long = 0,
    val ItemCode: String? = null,
    val ItemName: String? = null,
    val ItemNameArabic: String? = null,
    val Manfacturer: String? = null,
    val TrackingType: Long = 0,
    val UnitID: Int = 0,
//    val ItemPartCode: String? = null,
//    val ItemSerialNumber: String? = null,
    val UnitName: String? = null,
    val Factor: Double = 0.0,
    val Quantity: Double = 0.0,
    val RequiredQuantity: Double = 0.0,
    val Varience: Double = 0.0,
    val Cost: Double = 0.0,
    val TotalCost: Double = 0.0,
    val SalesPrice: Double = 0.0,
    val SQM: Double = 0.0,
    val TotalSalesPrice: Double = 0.0,
    val IsSerialItem: Int = 0,
//    val SerialItems: List<TransferSerialItemListDto>?
    val SerialItems: List<SerialItemsDto>?
) : Parcelable, Serializable {
    companion object

    fun getItemListName() = "$ItemCode - $ItemName"
    fun getQuantityString() = "$Quantity"

}
//
//@Parcelize
//@Keep
//data class TransferSerialItemListDto(
////    val ItemSerialID: String? = null,
////    val ItemID: String? = null,
////    val SerialNumber: String = "0",
////    val StockInHand: String? = null,
////    val MFGDate: String? = null,
////    val WarrantyDays: String? = null,
////    val ActualQuantity: String? = null,
////    val TrackingType: String? = null,
////    val IsChecked: String? = null,
//    val SerialNumber: String = "",
//    val ManufactureDate: String = "",
//    val WarrantyPeriod: String = "",
//    val Quantity: Double = 0.0,
//) : Parcelable, Serializable {
//    companion object
//
//    fun getConvertedTransferInSerialItemDto()=  TransferInSerialItemDto(SerialNumber=SerialNumber, ManufactureDate = ManufactureDate, WarrantyPeriod =WarrantyPeriod ,Quantity= Quantity)
//
//    fun getConvertedWarehouseSerialItemDetails()= WarehouseSerialItemDetails(WarehouseID=0,SerialNumber=SerialNumber,MFGDate=ManufactureDate,
//        WarentyDays=WarrantyPeriod,selected = false)
//}

@Keep
data class
TransferInRequestDto(
    val StockAdjustmentID: Long = 0,
    val ItemsDetails: List<TransferInItemsDetailsDto>
) {
    companion object
}



@Parcelize
@Keep
data class
SaveTransferInRequestDto(
    val TransferInID: Long = 0,
    val ToWarehouseID: Long = 0,
    val FromWarehouseID: Long = 0,
    val TransferOutID: Long = 0,
    val ItemsDetails: List<TransferInItemDto>?
) : Parcelable {
    companion object
}


@Parcelize
@Keep
data class TransferInItemDto(
    val ItemSeqNumber: Int = 0,
    val ItemID: Long = 0,
    val ItemCode: String,
    val Quantity: Double,
    val SerialItems: List<SerialItemsDto>
) : Parcelable {
    companion object
}

@Parcelize
@Keep
data class TransferInSerialItemDto(
    val SerialNumber:String? = "",
    val ManufactureDate: String? = "",
    val WarrantyPeriod: String? = "",
    val Quantity:Double? = 0.0,
) : Parcelable {
    companion object
}

@Keep
data class
TransferOutNumbersRequestDto(
    val ToWarehouseID: Long = 0
) {
    companion object
}

@Keep
data class
TransferOutItemsRequestDto(
    val ExternalTransferID: Long = 0
) {
    companion object
}

@Keep
data class
TransferOutNumbersResponseDto(
    val success: Boolean,
    val ExternalTransfers: List<ExternalTransfersDto>?=null
) {
    companion object
}

@Keep
data class
ExternalTransfersDto(
    val ExternalTransferID: Long = 0,
    val ExternalTransferNumber: String = ""
) {
    companion object
}

@Keep
data class SaveTransferInResponse(
    val Success: Boolean,
    val TransferInID: String?,
    val ErrorMessage: String="",
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
    val displayName: String
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