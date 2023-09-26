package com.exert.wms.delivery.api

import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val transferDataSource: DeliveryDataSource) {

    fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): Flow<SaveDeliveryReceiptItemsResponse> {
        return transferDataSource.saveDeliveryReceiptItems(requestDto)
    }

    fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsListRequestDto): Flow<SaveDeliveryNoteItemsResponse> {
        return transferDataSource.saveDeliveryNoteItems(requestDto)
    }

    fun getSalesOrdersList(requestDto: SalesOrdersRequestDto): Flow<SalesOrdersListResponseDto> {
        return transferDataSource.getSalesOrdersList(requestDto)
    }

    fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsListRequestDto): Flow<DeliveryNoteItemsResponseDto> {
        return transferDataSource.getDeliveryNotesItemsList(requestDto)
    }
}