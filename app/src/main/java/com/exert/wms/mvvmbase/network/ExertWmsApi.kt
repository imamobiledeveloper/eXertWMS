package com.exert.wms.mvvmbase.network

import com.exert.wms.itemStocks.api.ItemStocksRequestDto
import com.exert.wms.itemStocks.api.ItemStocksResponseDto
import com.exert.wms.login.api.FinancialPeriodDto
import com.exert.wms.login.api.LoginDto
import com.exert.wms.login.api.LoginRequestDto
import com.exert.wms.stockAdjustment.api.StockAdjustmentRequestDto
import com.exert.wms.stockAdjustment.api.StockItemAdjustmentDto
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

    @POST("webapi/api/Login/LoginAuthentication")
    suspend fun getOnlineSalesItems(
        @Body requestBody: ItemStocksRequestDto
    ): ItemStocksResponseDto

    @POST("webapi/api/Login/LoginAuthentication")
    suspend fun getStockAdjustmentItems(
        @Body requestBody: StockAdjustmentRequestDto
    ): StockItemAdjustmentDto
}