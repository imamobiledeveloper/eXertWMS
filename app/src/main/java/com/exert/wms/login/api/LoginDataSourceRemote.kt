package com.exert.wms.login.api

import com.exert.wms.mvvmbase.network.ExertWmsApi

class LoginDataSourceRemote(private val exertWmsApi: ExertWmsApi) {
    suspend fun getApiAccess(): String {
        return exertWmsApi.getApiAccess()
    }

    suspend fun getFinancialPeriod(): FinancialPeriodDto {
        return exertWmsApi.getFinancialPeriod()
    }
    suspend fun getUserIdUsingEmailId(email : String): ForgotPasswordDto {
        return exertWmsApi.getUserIdByEmail(email)
    }
    suspend fun setNewPassword(requestDto : ForgotPasswordRequestDto): SuccessResponse {
        return exertWmsApi.setNewPassword(requestDto)
    }

    suspend fun authenticateUser(requestDto: LoginRequestDto): LoginDto {
        return exertWmsApi.authenticateUser(requestDto)
    }
}