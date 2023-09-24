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
    fun getVendorsList(): Flow<VendorsListDto> {
        return flow {
            emit(
                warehouseDataSourceRemote.getVendorsList()
            )
        }
    }
    fun getBranchesList(): Flow<BranchesListDto> {
        return flow {
            emit(
                warehouseDataSourceRemote.getBranchesList()
            )
        }
    }
}