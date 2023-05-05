package com.exert.wms.mvvmbase.network

import com.exert.wms.login.FinancialPeriodDto
import com.exert.wms.login.LoginDto
import com.exert.wms.login.LoginRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ExertWmsApi {
    @GET("Login/GetFinancialPeriod")
    suspend fun getFinancialPeriod(): FinancialPeriodDto

    @POST("Login/LoginAuthentication")
    suspend fun authenticateUser(
        @Body requestBody: LoginRequestDto
    ): LoginDto
}