package com.exert.wms.stockAdjustment.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StockAdjustmentDataSource(
    private val stockAdjustmentDataSourceRemote: StockAdjustmentDataSourceRemote
) {
    fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<SaveStockItemAdjustmentResponse> {
        return flow {
            emit(
                stockAdjustmentDataSourceRemote.saveStockAdjustmentItems(requestDto)
            )
        }
    }

}