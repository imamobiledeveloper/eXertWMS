package com.exert.wms

import android.os.Parcelable
import androidx.annotation.Keep
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*


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

    fun getFormattedManufactureDate()=ManufactureDate?.let { convertDateFormat(it) } ?: ""
}

@Parcelize
@Keep
data class SerialItemsDtoList(
    val serialItemsDto: List<SerialItemsDto>?,
    val itemId: Long? = null
) : Parcelable {
    companion object
}

fun convertDateFormat(inputDateString: String): String {
    // Define the input and output date format patterns
    val inputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    try {
        // Parse the input date string
        val date: Date? = inputFormat.parse(inputDateString)

        // Format the date to the desired output format
        return date?.let { outputFormat.format(it) } ?: inputDateString
    } catch (e: Exception) {
        // Handle any parsing errors here
        return inputDateString // Return an empty string or handle the error as needed
    }
}