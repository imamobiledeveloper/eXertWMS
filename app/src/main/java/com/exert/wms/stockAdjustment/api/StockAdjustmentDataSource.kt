package com.exert.wms.stockAdjustment.api

import com.exert.wms.warehouse.WarehouseListDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class  StockAdjustmentDataSource(
    private val stockAdjustmentDataSourceRemote: StockAdjustmentDataSourceRemote
) {
    fun saveStockAdjustmentItems(requestDto: StockAdjustmentRequestDto): Flow<StockItemAdjustmentDto> {
        return flow {
            emit(
                stockAdjustmentDataSourceRemote.saveStockAdjustmentItems(requestDto)
            )
        }
    }

//    fun getWarehouseList(): Flow<WarehouseListDto> {
//        return flow {
//            emit(
//                stockAdjustmentDataSourceRemote.getWarehouseList()
//            )
//        }
//    }
}