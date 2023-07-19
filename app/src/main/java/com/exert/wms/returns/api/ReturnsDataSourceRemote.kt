package com.exert.wms.returns.api

import com.exert.wms.delivery.api.PurchaseItemsRequestDto
import com.exert.wms.delivery.api.SalesItemsRequestDto
import com.exert.wms.delivery.api.SavePurchaseItemsResponse
import com.exert.wms.delivery.api.SaveSalesItemsResponse
import com.exert.wms.mvvmbase.network.ExertWmsApi

class ReturnsDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun savePurchaseItems(requestDto: PurchaseItemsRequestDto): SavePurchaseItemsResponse {
        return exertWmsApi.savePurchaseItems(requestDto)
    }

    suspend fun saveSalesItems(requestDto: SalesItemsRequestDto): SaveSalesItemsResponse {
        return exertWmsApi.saveSalesItems(requestDto)
    }
}