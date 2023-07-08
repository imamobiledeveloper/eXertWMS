package com.exert.wms.warehouse

import com.exert.wms.mvvmbase.network.ExertWmsApi

class WarehouseDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun getWarehouseList(): WarehouseListDto {
        return exertWmsApi.getWarehouseList()
    }
}