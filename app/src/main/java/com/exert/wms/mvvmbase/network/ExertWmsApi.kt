package com.exert.wms.mvvmbase.network

import com.exert.wms.delivery.api.*
import com.exert.wms.itemStocks.api.ItemStocksRequestDto
import com.exert.wms.itemStocks.api.ItemStocksResponseDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemsRequestDto
import com.exert.wms.login.api.FinancialPeriodDto
import com.exert.wms.login.api.LoginDto
import com.exert.wms.login.api.LoginRequestDto
import com.exert.wms.returns.api.*
import com.exert.wms.stockAdjustment.api.SaveStockItemAdjustmentResponse
import com.exert.wms.stockAdjustment.api.StockAdjustmentRequestDto
import com.exert.wms.stockReconciliation.api.StockItemReconciliationDto
import com.exert.wms.stockReconciliation.api.StockReconciliationRequestDto
import com.exert.wms.transfer.api.*
import com.exert.wms.warehouse.BranchesListDto
import com.exert.wms.warehouse.CustomersListDto
import com.exert.wms.warehouse.VendorsListDto
import com.exert.wms.warehouse.WarehouseListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExertWmsApi {
    @GET("")
    suspend fun getApiAccess(): String

    @GET("api/Login/GetFinancialPeriod")
    suspend fun getFinancialPeriod(): FinancialPeriodDto

    @POST("api/Login/LoginAuthentication")
    suspend fun authenticateUser(
        @Body requestBody: LoginRequestDto
    ): LoginDto

    @POST("api/Items/GetItems?CurrentPage=1&PageSize=100")
    suspend fun getOnlineSalesItems(
        @Body requestBody: ItemStocksRequestDto
    ): ItemStocksResponseDto

    @POST("api/Items/GetItems?CurrentPage=1&PageSize=100")
    suspend fun getWarehouseSerialItemDetails(
        @Body requestBody: WarehouseSerialItemsRequestDto
    ): ItemStocksResponseDto

    @POST("api/StockAdjustment/SaveStockAdjustment")
    suspend fun saveStockAdjustmentItems(
        @Body requestBody: StockAdjustmentRequestDto
    ): SaveStockItemAdjustmentResponse

    @GET("api/Warehouse/GetWarehouse")
    suspend fun getWarehouseList(): WarehouseListDto

    @GET("api/Vendor/GetVendor")
    suspend fun getVendorsList(): VendorsListDto

    @GET("api/Customer/GetCreditCustomers")
    suspend fun getCustomersList(): CustomersListDto

    @GET("api/Branch/GetBranches")
    suspend fun getBranchesList(): BranchesListDto

    @POST("api/StockAdjustment/SaveStockAdjustment")
    suspend fun saveStockReconciliationItems(
        @Body requestBody: StockReconciliationRequestDto
    ): StockItemReconciliationDto

    @POST("api/TransferOut/SaveTransferOut")
    suspend fun saveTransferOutItems(
        @Body requestBody: TransferOutRequestDto
    ): SaveTransferOutResponse

    @POST("api/TransferIn/SaveTransferIn")
    suspend fun saveTransferInItems(
        @Body requestBody: SaveTransferInRequestDto
    ): SaveTransferInResponse

    @GET("api/TransferIn/GetPostedExternalTransfers")
    suspend fun getTransferOutNumbers(
        @Query("ToWarehouseID") ToWarehouseID: Long
    ): TransferOutNumbersResponseDto

    @GET("api/TransferIn/GetExternalTransferItems")
    suspend fun getTransferInItemsList(
        @Query("ExternalTransferID") ExternalTransferID: Long
    ): TransferInItemsResponseDto

    @POST("api/DeliveryReceipt/SaveDeliveryReceipt")
    suspend fun saveDeliveryReceiptItems(
        @Body requestBody: DeliveryReceiptItemsRequestDto
    ): SaveDeliveryReceiptItemsResponse

    @POST("api/DeliveryNote/SaveDeliveryNote")
    suspend fun saveDeliveryNoteItems(
        @Body requestBody: DeliveryNoteItemsListRequestDto
    ): SaveDeliveryNoteItemsResponse

    @GET("api/DeliveryReceipt/GetApprovedPurchaseOrders")
    suspend fun getSalesOrdersList(
        @Query("VendorID") VendorID: Long,
        @Query("BranchID") BranchID: Long
    ): PurchaseOrdersListResponseDto

    @GET("api/DeliveryNote/GetApprovedSalesOrders")
    suspend fun getSalesInvoiceNosList(
        @Query("CustomerID") CustomerID: Long,
        @Query("BranchID") BranchID: Long
    ): SalesOrdersListResponseDto

    @POST("api/DeliveryNote/GetMultipleSalesOrderItems")
    suspend fun getDeliveryNotesItemsList(
        @Body requestBody: DeliveryNoteItemsListWithOutItemsRequestDto
    ): DeliveryNoteItemsResponseDto

    @GET("api/PurchaseReturn/GetPostedPurchases")
    suspend fun getPurchaseInvoiceNoList(
        @Query("BranchID") BranchID: Long,
        @Query("VendorID") VendorID: Long
    ): PurchaseReturnInvoiceListResponseDto

    @POST("api/DeliveryReceipt/GetMultiplePurchaseOrderItems")
    suspend fun getDeliveryReceiptItemsList(
        @Body requestBody: DeliveryReceiptItemsListWithOutItemsRequestDto
    ): DeliveryReceiptItemsResponseDto

    @POST("api/PurchaseReturn/SavePurchaseReturn")
    suspend fun savePurchaseReturn(
        @Body requestBody: PurchaseItemsRequestDto
    ): SavePurchaseItemsResponse

    @POST("api/StockAdjustment/SaveSalesItems")
    suspend fun saveSalesItems(
        @Body requestBody: SalesItemsRequestDto
    ): SaveSalesItemsResponse

    @GET("api/PurchaseReturn/GetPurchaseItems")
    suspend fun getPurchaseItemsList(
        @Query("PurchaseID") PurchaseID: Long,
    ): PurchaseItemsListResponseDto
}