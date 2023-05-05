package com.exert.wms.mvvmbase.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithAuth = chain.request().newBuilder()
//            .addHeader("", "")
            .build()
        return chain.proceed(requestWithAuth)
    }
}