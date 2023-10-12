package com.exert.wms.returns.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class ReturnsDataSourceRemote(private val exertWmsApi: ExertWmsApi) {

    suspend fun savePurchaseItems(requestDto: PurchaseItemsRequestDto): SavePurchaseItemsResponse {
        return exertWmsApi.savePurchaseReturn(requestDto)
    }

    suspend fun saveSalesItems(requestDto: SalesItemsRequestDto): SaveSalesItemsResponse {
        return exertWmsApi.saveSalesItems(requestDto)
    }

    suspend fun getPurchaseInvoiceNoList(requestDto: PurchaseReturnInvoiceRequestDto): PurchaseReturnInvoiceListResponseDto {
        return exertWmsApi.getPurchaseInvoiceNoList(BranchID=requestDto.BranchID, VendorID=requestDto.VendorID)
    }

    suspend fun getPurchaseItemsList(requestDto: PurchaseItemsListItemsRequestDto): PurchaseItemsListResponseDto {
        return exertWmsApi.getPurchaseItemsList(PurchaseID=requestDto.PurchaseID)
    }

    suspend fun getSalesInvoiceNoList(requestDto: SalesReturnInvoiceRequestDto): SalesReturnInvoiceListResponseDto {
        return exertWmsApi.getSalesInvoiceNoList(BranchID=requestDto.BranchID, CustomerID =requestDto.CustomerID)
    }

    suspend fun getSalesItemsList(requestDto: SalesItemsListItemsRequestDto): SalesItemsListResponseDto {
        return exertWmsApi.getSalesItemsList(SalesID =requestDto.SalesID)
    }
}