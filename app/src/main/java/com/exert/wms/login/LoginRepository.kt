package com.exert.wms.login

import kotlinx.coroutines.flow.Flow


class LoginRepository(private val loginDatasource: LoginDataSource) {

    fun authenticateUser(requestDto:LoginRequestDto): Flow<LoginDto> {
        return loginDatasource.authenticateUser(requestDto)
    }

    fun getFinancialPeriod(): Flow<FinancialPeriodDto> {
        return loginDatasource.getFinancialPeriod()
    }

}