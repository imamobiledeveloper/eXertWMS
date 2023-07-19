package com.exert.wms.mvvmbase.network

import com.exert.wms.delivery.api.*
import com.exert.wms.itemStocks.api.ItemStocksRequestDto
import com.exert.wms.itemStocks.api.ItemStocksResponseDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemsRequestDto
import com.exert.wms.login.api.FinancialPeriodDto
import com.exert.wms.login.api.LoginDto
import com.exert.wms.login.api.LoginRequestDto
import com.exert.wms.returns.api.DeliveryNoteItemsRequestDto
import com.exert.wms.returns.api.DeliveryReceiptItemsRequestDto
import com.exert.wms.returns.api.SaveDeliveryNoteItemsResponse
import com.exert.wms.returns.api.SaveDeliveryReceiptItemsResponse
import com.exert.wms.stockAdjustment.api.SaveStockItemAdjustmentResponse
import com.exert.wms.stockAdjustment.api.StockAdjustmentRequestDto
import com.exert.wms.stockReconciliation.api.StockItemReconciliationDto
import com.exert.wms.stockReconciliation.api.StockReconciliationRequestDto
import com.exert.wms.transfer.api.SaveTransferInResponse
import com.exert.wms.transfer.api.SaveTransferOutResponse
import com.exert.wms.transfer.api.TransferInRequestDto
import com.exert.wms.transfer.api.TransferOutRequestDto
import com.exert.wms.warehouse.WarehouseListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ExertWmsApi {
    @GET("webapi/")
    suspend fun getApiAccess(): String

    @GET("webapi/api/Login/GetFinancialPeriod")
    suspend fun getFinancialPeriod(): FinancialPeriodDto

    @POST("webapi/api/Login/LoginAuthentication")
    suspend fun authenticateUser(
        @Body requestBody: LoginRequestDto
    ): LoginDto

    @POST("webapi/api/Items/GetItems?CurrentPage=1&PageSize=10")
    suspend fun getOnlineSalesItems(
        @Body requestBody: ItemStocksRequestDto
    ): ItemStocksResponseDto

    @POST("webapi/api/Items/GetItems?CurrentPage=1&PageSize=10")
    suspend fun getWarehouseSerialItemDetails(
        @Body requestBody: WarehouseSerialItemsRequestDto
    ): ItemStocksResponseDto

    @POST("webapi/api/StockAdjustment/SaveStockAdjustment")
    suspend fun saveStockAdjustmentItems(
        @Body requestBody: StockAdjustmentRequestDto
    ): SaveStockItemAdjustmentResponse

    @GET("webapi/api/Warehouse/GetWarehouse")
    suspend fun getWarehouseList(): WarehouseListDto

    @POST("webapi/api/StockAdjustment/SaveStockAdjustment")
    suspend fun saveStockReconciliationItems(
        @Body requestBody: StockReconciliationRequestDto
    ): StockItemReconciliationDto

    @POST("webapi/api/StockAdjustment/SaveTransferOutItems")
    suspend fun saveTransferOutItems(
        @Body requestBody: TransferOutRequestDto
    ): SaveTransferOutResponse

    @POST("webapi/api/StockAdjustment/SaveTransferInItems")
    suspend fun saveTransferInItems(
        @Body requestBody: TransferInRequestDto
    ): SaveTransferInResponse

    @POST("webapi/api/StockAdjustment/SaveDeliveryReceiptItems")
    suspend fun saveDeliveryReceiptItems(
        @Body requestBody: DeliveryReceiptItemsRequestDto
    ): SaveDeliveryReceiptItemsResponse

    @POST("webapi/api/StockAdjustment/SaveDeliveryNoteItems")
    suspend fun saveDeliveryNoteItems(
        @Body requestBody: DeliveryNoteItemsRequestDto
    ): SaveDeliveryNoteItemsResponse

    @POST("webapi/api/StockAdjustment/SavePurchaseItems")
    suspend fun savePurchaseItems(
        @Body requestBody: PurchaseItemsRequestDto
    ): SavePurchaseItemsResponse

    @POST("webapi/api/StockAdjustment/SaveSalesItems")
    suspend fun saveSalesItems(
        @Body requestBody: SalesItemsRequestDto
    ): SaveSalesItemsResponse

}