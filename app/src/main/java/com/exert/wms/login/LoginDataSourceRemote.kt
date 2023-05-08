package com.exert.wms.login

import com.exert.wms.mvvmbase.network.ExertWmsApi

class LoginDataSourceRemote(private val exertWmsApi: ExertWmsApi) {
    suspend fun getApiAccess(): String {
        return exertWmsApi.getApiAccess()
    }

    suspend fun getFinancialPeriod(): FinancialPeriodDto {
        return exertWmsApi.getFinancialPeriod()
    }

    suspend fun authenticateUser(requestDto:LoginRequestDto): LoginDto {
        return exertWmsApi.authenticateUser(requestDto)
    }
}