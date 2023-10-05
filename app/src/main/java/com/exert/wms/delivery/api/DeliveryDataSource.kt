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

    fun saveDeliveryNoteItems(requestDto: DeliveryNoteItemsListRequestDto): Flow<SaveDeliveryNoteItemsResponse> {
        return flow {
            emit(
                deliveryDataSourceRemote.saveDeliveryNoteItems(requestDto)
            )
        }
    }

    fun getSalesOrdersList(requestDto: SalesOrdersRequestDto): Flow<PurchaseOrdersListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getSalesOrdersList(requestDto)
            )
        }
    }

    fun getSalesInvoiceNosList(requestDto: SalesInvoiceRequestDto): Flow<SalesOrdersListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getSalesInvoiceNosList(requestDto)
            )
        }
    }

    fun getDeliveryNotesItemsList(requestDto: DeliveryNoteItemsListWithOutItemsRequestDto): Flow<DeliveryNoteItemsResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getDeliveryNotesItemsList(requestDto)
            )
        }
    }

    fun getDeliveryReceiptItemsList(requestDto: DeliveryReceiptItemsListWithOutItemsRequestDto): Flow<DeliveryReceiptItemsResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getDeliveryReceiptItemsList(requestDto)
            )
        }
    }

}