package com.exert.wms.returns.api

import com.exert.wms.delivery.api.PurchaseItemsRequestDto
import com.exert.wms.delivery.api.SalesItemsRequestDto
import com.exert.wms.delivery.api.SavePurchaseItemsResponse
import com.exert.wms.delivery.api.SaveSalesItemsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReturnsDataSource(
    private val deliveryDataSourceRemote: ReturnsDataSourceRemote
) {
    fun savePurchaseItems(requestDto: PurchaseItemsRequestDto): Flow<SavePurchaseItemsResponse> {
        return flow {
            emit(
                deliveryDataSourceRemote.savePurchaseItems(requestDto)
            )
        }
    }

    fun saveSalesItems(requestDto: SalesItemsRequestDto): Flow<SaveSalesItemsResponse> {
        return flow {
            emit(
                deliveryDataSourceRemote.saveSalesItems(requestDto)
            )
        }
    }

}