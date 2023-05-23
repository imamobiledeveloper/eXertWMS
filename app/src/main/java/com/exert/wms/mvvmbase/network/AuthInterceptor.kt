package com.exert.wms.mvvmbase.network

import com.exert.wms.utils.UserDefaults
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val userDefaults: UserDefaults) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithAuth = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${userDefaults.getUserToken()}")
            .build()
        return chain.proceed(requestWithAuth)
    }
}