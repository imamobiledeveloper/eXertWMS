package com.exert.wms.stockReconciliation.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StockReconciliationDataSource(
    private val stockAdjustmentDataSourceRemote: StockReconciliationDataSourceRemote
) {
    fun saveStockAdjustmentItems(requestDto: StockReconciliationRequestDto): Flow<StockItemReconciliationDto> {
        return flow {
            emit(
                stockAdjustmentDataSourceRemote.saveStockAdjustmentItems(requestDto)
            )
        }
    }
}