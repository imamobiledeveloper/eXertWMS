package com.exert.wms.stockAdjustment.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class  StockAdjustmentDataSource(
    private val stockAdjustmentDataSourceRemote: StockAdjustmentDataSourceRemote
) {
    fun getStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<StockItemAdjustmentDto> {
        return flow {
            emit(
                stockAdjustmentDataSourceRemote.getStockAdjustmentItems(requestDto)
            )
        }
    }
}