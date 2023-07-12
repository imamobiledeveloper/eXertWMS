package com.exert.wms.stockAdjustment.api

import kotlinx.coroutines.flow.Flow

class StockAdjustmentRepository(private val stockAdjustmentDataSource: StockAdjustmentDataSource) {

    fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<StockItemAdjustmentDto> {
        return stockAdjustmentDataSource.saveStockAdjustmentItems(requestDto)
    }
}