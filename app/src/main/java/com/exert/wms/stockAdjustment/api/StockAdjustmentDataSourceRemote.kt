package com.exert.wms.stockAdjustment.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class StockAdjustmentDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): SaveStockItemAdjustmentResponse {
        return exertWmsApi.saveStockAdjustmentItems(requestDto)
    }
}