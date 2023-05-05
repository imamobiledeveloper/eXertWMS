package com.exert.wms.login

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginDataSource(
    private val loginDataSourceRemote: LoginDataSourceRemote,
    private val loginDataSourceLocal: LoginDataSourceLocal
) {
    fun authenticateUser(requestDto:LoginRequestDto): Flow<LoginDto> {
        return flow {
            emit(
                authenticateUserFromRemote(requestDto)
            )
        }
    }

    private suspend fun authenticateUserFromRemote(requestDto:LoginRequestDto): LoginDto {
        val response = loginDataSourceRemote.authenticateUser(requestDto)
        loginDataSourceLocal.saveLoginInfo(response)
        return response
    }

    fun getFinancialPeriod(): Flow<FinancialPeriodDto> {
        return flow {
            emit(
                getFinancialPeriodFromRemote()
            )
        }
    }

    private suspend fun getFinancialPeriodFromRemote(): FinancialPeriodDto {
        val response = loginDataSourceRemote.getFinancialPeriod()
//        loginDataSourceLocal.saveLoginInfo(response)
        return response
    }
}
