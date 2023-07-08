package com.exert.wms.warehouse

import kotlinx.coroutines.flow.Flow

class WarehouseRepository(private val warehouseDataSource: WarehouseDataSource) {

    fun getWarehouseList(): Flow<WarehouseListDto> {
        return warehouseDataSource.getWarehouseList()
    }
}