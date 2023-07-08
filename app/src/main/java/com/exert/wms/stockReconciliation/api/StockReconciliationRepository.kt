package com.exert.wms.stockReconciliation.api

import kotlinx.coroutines.flow.Flow

class StockReconciliationRepository(private val stockAdjustmentDataSource: StockReconciliationDataSource) {

    fun saveStockReconciliationItems(requestDto: StockReconciliationRequestDto): Flow<StockItemReconciliationDto> {
        return stockAdjustmentDataSource.saveStockAdjustmentItems(requestDto)
    }
}