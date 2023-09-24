package com.exert.wms.warehouse

import kotlinx.coroutines.flow.Flow

class WarehouseRepository(private val warehouseDataSource: WarehouseDataSource) {

    fun getWarehouseList(): Flow<WarehouseListDto> {
        return warehouseDataSource.getWarehouseList()
    }

    fun getVendorsList(): Flow<VendorsListDto> {
        return warehouseDataSource.getVendorsList()
    }

    fun getBranchesList(): Flow<BranchesListDto> {
        return warehouseDataSource.getBranchesList()
    }
}