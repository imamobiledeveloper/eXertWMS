package com.exert.wms.delivery.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeliveryDataSource(
    private val deliveryDataSourceRemote: DeliveryDataSourceRemote
) {
    fun saveDeliveryReceiptItems(requestDto: DeliveryReceiptItemsRequestDto): Flow<SaveDeliveryReceiptItemsResponse> {
        return flow {
            emit(
                deliveryDataSourceRemote.saveDeliveryReceiptItems(requestDto)
            )
        }
    }

    fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsRequestDto): Flow<SaveDeliveryNoteItemsResponse> {
        return flow {
            emit(
                deliveryDataSourceRemote.saveDeliveryNoteItems(requestDto)
            )
        }
    }

    fun getSalesOrdersList(requestDto: SalesOrdersRequestDto): Flow<SalesOrdersListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getSalesOrdersList(requestDto)
            )
        }
    }

    fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsRequestDto): Flow<DeliveryNoteItemsResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getDeliveryNotesItemsList(requestDto)
            )
        }
    }

}