package com.exert.wms.warehouse

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class  WarehouseDataSource(
    private val warehouseDataSourceRemote: WarehouseDataSourceRemote
) {
    fun getWarehouseList(): Flow<WarehouseListDto> {
        return flow {
            emit(
                warehouseDataSourceRemote.getWarehouseList()
            )
        }
    }
}