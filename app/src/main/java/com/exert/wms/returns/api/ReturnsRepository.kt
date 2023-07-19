package com.exert.wms.returns.api

import com.exert.wms.delivery.api.PurchaseItemsRequestDto
import com.exert.wms.delivery.api.SalesItemsRequestDto
import com.exert.wms.delivery.api.SavePurchaseItemsResponse
import com.exert.wms.delivery.api.SaveSalesItemsResponse
import kotlinx.coroutines.flow.Flow

class ReturnsRepository(private val transferDataSource: ReturnsDataSource) {

    fun savePurchaseItems(requestDto: PurchaseItemsRequestDto): Flow<SavePurchaseItemsResponse> {
        return transferDataSource.savePurchaseItems(requestDto)
    }

    fun saveSalesItems(requestDto: SalesItemsRequestDto): Flow<SaveSalesItemsResponse> {
        return transferDataSource.saveSalesItems(requestDto)
    }
}