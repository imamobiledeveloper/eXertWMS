package com.exert.wms.stockAdjustment.api

import kotlinx.coroutines.flow.Flow

class StockAdjustmentRepository(private val stockAdjustmentDataSource: StockAdjustmentDataSource) {

    fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<SaveStockItemAdjustmentResponse> {
        return stockAdjustmentDataSource.saveStockAdjustmentItems(requestDto)
    }
}