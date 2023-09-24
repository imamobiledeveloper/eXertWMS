package com.exert.wms.delivery.api

import com.exert.wms.mvvmbase.network.ExertWmsApi
import kotlinx.coroutines.flow.Flow

class DeliveryDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): SaveDeliveryReceiptItemsResponse {
        return exertWmsApi.saveDeliveryReceiptItems(requestDto)
    }

    suspend fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsRequestDto): SaveDeliveryNoteItemsResponse {
        return exertWmsApi.saveDeliveryNoteItems(requestDto)
    }

    suspend fun getSalesOrdersList(requestDto: SalesOrdersRequestDto): SalesOrdersListResponseDto {
        return exertWmsApi.getSalesOrdersList(CustomerID = 131, BranchID = requestDto.BranchID)//requestDto.CustomerID
    }

    suspend fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsRequestDto): DeliveryNoteItemsResponseDto {
        return exertWmsApi.getDeliveryNotesItemsList(requestDto)
    }
}