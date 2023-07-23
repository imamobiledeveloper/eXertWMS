package com.exert.wms.delivery.api

import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val transferDataSource: DeliveryDataSource) {

    fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): Flow<SaveDeliveryReceiptItemsResponse> {
        return transferDataSource.saveDeliveryReceiptItems(requestDto)
    }

    fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsRequestDto): Flow<SaveDeliveryNoteItemsResponse> {
        return transferDataSource.saveDeliveryNoteItems(requestDto)
    }
}