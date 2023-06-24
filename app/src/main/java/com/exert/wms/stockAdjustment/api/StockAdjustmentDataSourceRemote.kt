package com.exert.wms.stockAdjustment.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class StockAdjustmentDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): StockItemAdjustmentDto {
        return exertWmsApi.saveStockAdjustmentItems(requestDto)
    }

    suspend fun getWarehouseList(): WarehouseListDto {
        return exertWmsApi.getWarehouseList()
    }
}