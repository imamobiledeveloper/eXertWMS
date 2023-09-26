package com.exert.wms.delivery.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class DeliveryDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): SaveDeliveryReceiptItemsResponse {
        return exertWmsApi.saveDeliveryReceiptItems(requestDto)
    }

    suspend fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsListRequestDto): SaveDeliveryNoteItemsResponse {
        return exertWmsApi.saveDeliveryNoteItems(requestDto)
    }

    suspend fun getSalesOrdersList(requestDto: SalesOrdersRequestDto): SalesOrdersListResponseDto {
        return exertWmsApi.getSalesOrdersList(CustomerID = requestDto.CustomerID, BranchID = requestDto.BranchID)
    }

    suspend fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsListRequestDto): DeliveryNoteItemsResponseDto {
        return exertWmsApi.getDeliveryNotesItemsList(requestDto)
    }
}