package com.exert.wms.returns.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class ReturnsDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun savePurchaseItems(requestDto: PurchaseItemsRequestDto): SavePurchaseItemsResponse {
        return exertWmsApi.savePurchaseItems(requestDto)
    }

    suspend fun saveSalesItems(requestDto: SalesItemsRequestDto): SaveSalesItemsResponse {
        return exertWmsApi.saveSalesItems(requestDto)
    }
}