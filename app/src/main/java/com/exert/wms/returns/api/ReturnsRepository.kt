package com.exert.wms.returns.api

import kotlinx.coroutines.flow.Flow

class ReturnsRepository(private val transferDataSource: ReturnsDataSource) {

    fun savePurchaseItems(requestDto: PurchaseItemsRequestDto): Flow<SavePurchaseItemsResponse> {
        return transferDataSource.savePurchaseItems(requestDto)
    }

    fun saveSalesItems(requestDto: SalesItemsRequestDto): Flow<SaveSalesItemsResponse> {
        return transferDataSource.saveSalesItems(requestDto)
    }

    fun getPurchaseInvoiceNoList(requestDto: PurchaseReturnInvoiceRequestDto): Flow<PurchaseReturnInvoiceListResponseDto> {
        return transferDataSource.getPurchaseInvoiceNoList(requestDto)
    }

    fun getPurchaseItemsList(requestDto: PurchaseItemsListItemsRequestDto): Flow<PurchaseItemsListResponseDto> {
        return transferDataSource.getPurchaseItemsList(requestDto)
    }
}