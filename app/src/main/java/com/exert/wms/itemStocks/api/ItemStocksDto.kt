package com.exert.wms.itemStocks.api

import androidx.annotation.Keep

@Keep
data class ItemStocksRequestDto(
    val ItemCode: String
) {
    companion object
}

@Keep
data class ItemStocksResponseDto(
    val success: Boolean,
    val itemsList: List<ItemsDto>
) {
    companion object
}

@Keep
data class ItemsDto(
    val ItemID: Long,
    val ItemCode: String,
    val EANCode: String,
    val Stock: Double,

    ) {
    companion object
}