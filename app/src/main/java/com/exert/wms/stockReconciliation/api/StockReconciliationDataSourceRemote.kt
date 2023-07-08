package com.exert.wms.stockReconciliation.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class StockReconciliationDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveStockAdjustmentItems(requestDto: StockReconciliationRequestDto): StockItemReconciliationDto {
        return exertWmsApi.saveStockReconciliationItems(requestDto)
    }
}