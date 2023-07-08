package com.exert.wms.warehouse

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class WarehouseListDto(
    val success: Boolean,
    val Warehouses: List<WarehouseDto>,
) {
    companion object
}

@Parcelize
@Keep
data class WarehouseDto(
    val WarehouseID: Long,
    val Warehouse: String,
) : Serializable, Parcelable {
    override fun toString(): String {
        return this.Warehouse
    }

    companion object
}