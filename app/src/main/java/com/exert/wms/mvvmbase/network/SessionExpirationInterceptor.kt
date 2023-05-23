package com.exert.wms.mvvmbase.network

import com.exert.wms.LogoutManager
import okhttp3.Interceptor
import okhttp3.Response

private const val UNAUTHORIZED = 401

class SessionExpirationInterceptor(private val logoutManager: LogoutManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code() == UNAUTHORIZED) {
            logoutManager.sessionExpired()
        }
        return response
    }
}

class SessionExpiredException : java.lang.Exception()