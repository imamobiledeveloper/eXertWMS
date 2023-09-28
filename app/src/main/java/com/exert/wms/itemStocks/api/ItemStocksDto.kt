package com.exert.wms.itemStocks.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.transfer.api.TransferSerialItemListDto
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class ItemStocksRequestDto(
    val ItemPartCode: String = "",
    val ItemSerialNumber: String = ""
) {
    companion object
}

@Keep
data class ItemStocksResponseDto(
    val success: Boolean,
    val Items: List<ItemsDto>? = null
) {
    companion object
}

@Parcelize
@Keep
data class ItemsDto(
    val ItemID: Long = 0,
    val UnitID: Long = 0,
    val ItemCode: String? = null,
    val ItemPartCode: String? = null,
    val ItemSerialNumber: String? = null,
    val ItemName: String? = null,
    val ItemNameAlias: String? = null,
    val Stock: Double = 0.0,
    val SalesPrice: Double = 0.0,
    val ShortCode: String? = null,
    val OldCode: String? = null,
    val Category: String? = null,
    val Manufacturer: String? = null,
    val SubCategory: String? = null,
    val CategoryID: Long = 0,
    val SubCategoryID: Long = 0,
    val VendorID: Long = 0,
    val WarehouseID: Long = 0,
    val count: Long = 0,
    val SNO: Long = 0,
    val IsSerialItem: Int = 0,
    val wStockDetails: List<WarehouseStockDetails>?,
    var convertedStockDetails: List<TransferSerialItemListDto>? = emptyList(),
    var Warehouse: String? = ""
) : Serializable, Parcelable {
    companion object

    fun getStockString() = Stock.toString()
    fun getWarehouseString() = Warehouse
    fun getItemListName() = "$ItemCode - $ItemName"

}

@Parcelize
@Keep
data class WarehouseStockDetails(
    val WarehouseID: Long = 0,
    val WarehouseCode: String? = null,
    val WarehouseDescription: String? = null,
    val WarehouseAlias: String? = null,
    val LocationID: Long = 0,
    val LocationCode: String? = null,
    val LocationName: String? = null,
    val LocationAlias: String? = null,
    val BranchID: Long = 0,
    val BranchCode: String? = null,
    val Branch: String? = null,
    val BranchAlias: String? = null,
    val FinalQuantity: Double = 0.0,
    val wSerialItemDetails: List<WarehouseSerialItemDetails>?,
) : Parcelable, Serializable {
    companion object
}

@Parcelize
@Keep
data class WarehouseSerialItemDetails(
    val WarehouseID: Long = 0,
    val SerialNumber: String? = null,
    val MFGDate: String? = null,
    val WarentyDays: String? = null,
    var selected: Boolean = false,
) : Parcelable, Serializable {

    companion object
}

@Keep
data class WarehouseSerialItemsRequestDto(
    val ItemPartCode: String = "",
    val ItemSerialNumber: String = "",
    val WarehouseID: Long,
) {
    companion object
}

@Keep
data class ItemsBarCodeDto(
    val isItItemPartCode: Boolean = false,
    val ItemPartCodeData: String = "",
    val ItemSerialNoData: String = "",
) {}
