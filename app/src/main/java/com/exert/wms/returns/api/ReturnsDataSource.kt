package com.exert.wms.returns.api

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

    fun getPurchaseInvoiceNoList(requestDto: PurchaseReturnInvoiceRequestDto): Flow<PurchaseReturnInvoiceListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getPurchaseInvoiceNoList(requestDto)
            )
        }
    }

    fun getPurchaseItemsList(requestDto: PurchaseItemsListItemsRequestDto): Flow<PurchaseItemsListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getPurchaseItemsList(requestDto)
            )
        }
    }

    fun getSalesInvoiceNoList(requestDto: SalesReturnInvoiceRequestDto): Flow<SalesReturnInvoiceListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getSalesInvoiceNoList(requestDto)
            )
        }
    }

    fun getSalesItemsList(requestDto: SalesItemsListItemsRequestDto): Flow<SalesItemsListResponseDto> {
        return flow {
            emit(
                deliveryDataSourceRemote.getSalesItemsList(requestDto)
            )
        }
    }


}