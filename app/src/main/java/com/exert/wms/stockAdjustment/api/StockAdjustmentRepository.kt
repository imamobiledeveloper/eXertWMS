package com.exert.wms.stockAdjustment.api

import kotlinx.coroutines.flow.Flow

class StockAdjustmentRepository(private val stockAdjustmentDataSource: StockAdjustmentDataSource) {

    fun getWarehouseList(): Flow<WarehouseListDto> {
        return stockAdjustmentDataSource.getWarehouseList()
    }

    fun getStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<StockItemAdjustmentDto> {
        return stockAdjustmentDataSource.getStockAdjustmentItems(requestDto)
    }
}