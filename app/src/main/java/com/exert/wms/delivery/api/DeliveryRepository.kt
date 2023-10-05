package com.exert.wms.delivery.api

import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val transferDataSource: DeliveryDataSource) {

    fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): Flow<SaveDeliveryReceiptItemsResponse> {
        return transferDataSource.saveDeliveryReceiptItems(requestDto)
    }

    fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsListRequestDto): Flow<SaveDeliveryNoteItemsResponse> {
        return transferDataSource.saveDeliveryNoteItems(requestDto)
    }

    fun getSalesInvoiceNosList(requestDto: SalesInvoiceRequestDto): Flow<SalesOrdersListResponseDto> {
        return transferDataSource.getSalesInvoiceNosList(requestDto)
    }

    fun getPurchaseOrdersList(requestDto: SalesOrdersRequestDto): Flow<PurchaseOrdersListResponseDto> {
        return transferDataSource.getSalesOrdersList(requestDto)
    }

    fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsListWithOutItemsRequestDto): Flow<DeliveryNoteItemsResponseDto> {
        return transferDataSource.getDeliveryNotesItemsList(requestDto)
    }
    fun getDeliveryReceiptItemsList(requestDto: DeliveryReceiptItemsListWithOutItemsRequestDto): Flow<DeliveryReceiptItemsResponseDto> {
        return transferDataSource.getDeliveryReceiptItemsList(requestDto)
    }
}