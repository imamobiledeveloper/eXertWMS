package com.exert.wms.itemStocks.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class ItemStocksDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun getItemWarehouseList(requestDto: ItemStocksRequestDto): ItemStocksResponseDto {
        return exertWmsApi.getOnlineSalesItems(requestDto)
    }

    suspend fun getWarehouseSerialItemDetails(requestDto: WarehouseSerialItemsRequestDto): ItemStocksResponseDto {
        return exertWmsApi.getWarehouseSerialItemDetails(requestDto)
    }
}