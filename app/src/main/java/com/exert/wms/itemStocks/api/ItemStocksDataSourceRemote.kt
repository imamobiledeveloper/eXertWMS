package com.exert.wms.itemStocks.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class ItemStocksDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun getOnlineSalesItems(requestDto: ItemStocksRequestDto): ItemStocksResponseDto {
        return exertWmsApi.getOnlineSalesItems(requestDto)
    }
}