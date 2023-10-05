package com.exert.wms.warehouse

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Keep
data class WarehouseListDto(
    val success: Boolean,
    val Warehouses: List<WarehouseDto>?,
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

@Keep
data class VendorsListDto(
    val success: Boolean,
    val Vendors: List<VendorDto>?,
) {
    companion object
}

@Parcelize
@Keep
data class VendorDto(
    val ID: Long,
    val Vendor: String,
) : Serializable, Parcelable {
    override fun toString(): String {
        return this.Vendor
    }

    companion object
}


@Keep
data class CustomersListDto(
    val success: Boolean,
    val Customers: List<CustomerDto>?,
) {
    companion object
}

@Parcelize
@Keep
data class CustomerDto(
    val CustomerID: Long,
    val CustomerName: String,
) : Serializable, Parcelable {
    override fun toString(): String {
        return this.CustomerID.toString()
    }

    companion object
}

@Keep
data class BranchesListDto(
    val success: Boolean,
    val Branches: List<BranchDto>?,
) {
    companion object
}

@Parcelize
@Keep
data class BranchDto(
    val BranchID: Long,
    val BranchCode: String,
) : Serializable, Parcelable {
    override fun toString(): String {
        return this.BranchCode
    }

    companion object
}



@Keep
data class PurchaseOrdersListDto(
    val success: Boolean,
    val Branches: List<PurchaseOrderDto>?,
) {
    companion object
}

@Parcelize
@Keep
data class PurchaseOrderDto(
    val BranchID: Long,
    val BranchCode: String,
) : Serializable, Parcelable {
    override fun toString(): String {
        return this.BranchCode
    }

    companion object
}