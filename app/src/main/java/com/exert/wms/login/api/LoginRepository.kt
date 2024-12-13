package com.exert.wms.login.api

import kotlinx.coroutines.flow.Flow


class LoginRepository(private val loginDatasource: LoginDataSource) {

    fun authenticateUser(requestDto: LoginRequestDto): Flow<LoginDto> {
        return loginDatasource.authenticateUser(requestDto)
    }

    fun getFinancialPeriod(): Flow<FinancialPeriodDto> {
        return loginDatasource.getFinancialPeriod()
    }

    fun getApiAccess(): Flow<String> {
        return loginDatasource.getApiAccess()
    }

    fun getUserIdUsingEmailId(email : String): Flow<ForgotPasswordDto> {
        return loginDatasource.getUserIdUsingEmailId(email)
    }

    fun setNewPassword(requestDto : ForgotPasswordRequestDto): Flow<SuccessResponse> {
        return loginDatasource.setNewPassword(requestDto)
    }
}