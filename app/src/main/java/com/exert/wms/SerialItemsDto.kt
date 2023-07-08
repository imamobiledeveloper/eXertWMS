package com.exert.wms

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
@Keep
data class SerialItemsDto(
    val SerialNumber: String? = "",
    val ManufactureDate: String? = "",
    val WarrantyPeriod: String? = "",
    val Quantity: Long = 0,
) : Serializable, Parcelable {
    companion object
}