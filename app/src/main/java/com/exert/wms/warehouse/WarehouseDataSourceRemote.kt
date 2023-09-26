package com.exert.wms.warehouse

import com.exert.wms.mvvmbase.network.ExertWmsApi

class WarehouseDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun getWarehouseList(): WarehouseListDto {
        return exertWmsApi.getWarehouseList()
    }
    suspend fun getVendorsList(): VendorsListDto {
        return exertWmsApi.getVendorsList()
    }
    suspend fun getCustomersList(): CustomersListDto {
        return exertWmsApi.getCustomersList()
    }
    suspend fun getBranchesList(): BranchesListDto {
        return exertWmsApi.getBranchesList()
    }
}