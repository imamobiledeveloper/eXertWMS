package com.exert.wms

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import kotlinx.parcelize.Parcelize


@Parcelize
@Keep
data class SerialItemsDto(
    val SerialNumber: String? = "",
    val ManufactureDate: String? = "",
    val WarrantyPeriod: String? = "",
    val Quantity: Double = 0.0,
) : Parcelable {
    companion object

    fun getConvertedWarehouseSerialItemDetails() = WarehouseSerialItemDetails(
        WarehouseID = 0, SerialNumber = SerialNumber, MFGDate = ManufactureDate,
        WarentyDays = WarrantyPeriod, selected = false
    )
//    fun getConvertedTransferInSerialItemDto()=  TransferInSerialItemDto(SerialNumber=SerialNumber, ManufactureDate = ManufactureDate, WarrantyPeriod =WarrantyPeriod ,Quantity= Quantity)
}

@Parcelize
@Keep
data class SerialItemsDtoList(
    val serialItemsDto: List<SerialItemsDto>?,
    val itemId: Long? = null
) : Parcelable {
    companion object
}