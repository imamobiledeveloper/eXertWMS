package com.exert.wms.delivery.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class DeliveryDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): SaveDeliveryReceiptItemsResponse {
        return exertWmsApi.saveDeliveryReceiptItems(requestDto)
    }

    suspend fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsListRequestDto): SaveDeliveryNoteItemsResponse {
        return exertWmsApi.saveDeliveryNoteItems(requestDto)
    }

    suspend fun getSalesOrdersList(requestDto: SalesOrdersRequestDto): PurchaseOrdersListResponseDto {
        return exertWmsApi.getSalesOrdersList(VendorID = requestDto.VendorID, BranchID = requestDto.BranchID)
    }

    suspend fun getSalesInvoiceNosList(requestDto: SalesInvoiceRequestDto): SalesOrdersListResponseDto {
        return exertWmsApi.getSalesInvoiceNosList(CustomerID = requestDto.CustomerID, BranchID = requestDto.BranchID)
    }

    suspend fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsListWithOutItemsRequestDto): DeliveryNoteItemsResponseDto {
        return exertWmsApi.getDeliveryNotesItemsList(requestDto)
    }

    suspend fun getDeliveryReceiptItemsList(requestDto: DeliveryReceiptItemsListWithOutItemsRequestDto): DeliveryReceiptItemsResponseDto {
        return exertWmsApi.getDeliveryReceiptItemsList(requestDto)
    }
}