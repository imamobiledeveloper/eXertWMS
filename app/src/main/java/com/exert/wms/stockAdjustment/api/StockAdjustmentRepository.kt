package com.exert.wms.stockAdjustment.api

import com.exert.wms.warehouse.WarehouseListDto
import kotlinx.coroutines.flow.Flow

class StockAdjustmentRepository(private val stockAdjustmentDataSource: StockAdjustmentDataSource) {

//    fun getWarehouseList(): Flow<WarehouseListDto> {
//        return stockAdjustmentDataSource.getWarehouseList()
//    }

    fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<StockItemAdjustmentDto> {
        return stockAdjustmentDataSource.saveStockAdjustmentItems(requestDto)
    }
}