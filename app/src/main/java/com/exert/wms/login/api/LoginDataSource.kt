package com.exert.wms.login.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginDataSource(
    private val loginDataSourceRemote: LoginDataSourceRemote,
    private val loginDataSourceLocal: LoginDataSourceLocal
) {
    fun authenticateUser(requestDto: LoginRequestDto): Flow<LoginDto> {
        return flow {
            emit(
                authenticateUserFromRemote(requestDto)
            )
        }
    }

    private suspend fun authenticateUserFromRemote(requestDto: LoginRequestDto): LoginDto {
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
        return loginDataSourceRemote.getFinancialPeriod()
    }

    fun getApiAccess(): Flow<String> {
        return flow {
            emit(
                getApiAccessFromRemote()
            )
        }
    }

    private suspend fun getApiAccessFromRemote(): String {
        return loginDataSourceRemote.getApiAccess()
    }

    fun getUserIdUsingEmailId(email : String): Flow<ForgotPasswordDto> {
        return flow {
            emit(
                getUserIdUsingEmailIdFromRemote(email)
            )
        }
    }

    private suspend fun getUserIdUsingEmailIdFromRemote(email : String): ForgotPasswordDto {
        return loginDataSourceRemote.getUserIdUsingEmailId(email)
    }

    fun setNewPassword(requestDto : ForgotPasswordRequestDto): Flow<SuccessResponse> {
        return flow {
            emit(
                setNewPasswordInRemote(requestDto)
            )
        }
    }

    private suspend fun setNewPasswordInRemote(requestDto : ForgotPasswordRequestDto): SuccessResponse {
        return loginDataSourceRemote.setNewPassword(requestDto)
    }

    fun clearLoginCache() {
        loginDataSourceLocal.clear()
    }
}
