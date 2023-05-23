package com.exert.wms.mvvmbase.modules

import android.content.Context
import com.exert.wms.AppConfiguration
import com.exert.wms.mvvmbase.network.ExertWmsApi
import com.exert.wms.mvvmbase.network.HeaderInterceptor
import com.exert.wms.utils.Constants
import com.exert.wms.mvvmbase.network.AuthInterceptor
import com.exert.wms.mvvmbase.network.SessionExpirationInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val networkModule = module(override = true) {

    single {
        provideLoggingInterceptor()
    }

    single(named("exertApi")) {
        get<Retrofit>(named("exertRetrofit")).create(ExertWmsApi::class.java)
    }

    single(named("tokenApi")) {
        provideLoginOkHttpClient(
            loginInterceptor = get(),
            context = get(),
            authInterceptor = get(),
            headerInterceptor = HeaderInterceptor(),

            )
    }
    single(named("exertTokenRetrofit")) {
        provideTokenRetrofit(
            AppConfiguration.BASE_API_URL,
            get(named("tokenApi"))
        )
    }

    single(named("exertTokenApi")) {
        get<Retrofit>(named("exertTokenRetrofit")).create(ExertWmsApi::class.java)
    }

    single(named("exertUser")) {
        provideLoginOkHttpClient(
            loginInterceptor = get(),
            context = get(),
            authInterceptor = get(),
            headerInterceptor = HeaderInterceptor()
        )
    }

    single(named("exertRetrofit")) {
        provideRetrofit(
            AppConfiguration.BASE_API_URL,
            get(named("exertUser"))
        )
    }
}

fun provideLoginOkHttpClient(
    loginInterceptor: HttpLoggingInterceptor,
    context: Context,
    authInterceptor: AuthInterceptor,
    headerInterceptor: Interceptor
): OkHttpClient {
//    val clientTrustManager = CustomTrustManager(context, "")

    return OkHttpClient.Builder()
        .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(loginInterceptor)
//        .sslSocketFactory(clientTrustManager.)
        .build()
}

fun provideRetrofit(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .callFactory(OkHttpClient.Builder().build())
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(provideGson())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

fun provideGson(): GsonConverterFactory =
    GsonConverterFactory.create(
        GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
    )

private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

fun provideTokenRetrofit(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .callFactory(OkHttpClient.Builder().build())
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(provideGson())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

fun provideLoginOkHttpClient(
    loginInterceptor: HttpLoggingInterceptor,
    context: Context,
    authInterceptor: AuthInterceptor,
    sessionExpirationInterceptor: SessionExpirationInterceptor,
    headerInterceptor: Interceptor
): OkHttpClient {
//    val clientTrustManager = CustomTrustManager(context, "")

    return OkHttpClient.Builder()
        .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
        .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(authInterceptor)
        .addInterceptor(sessionExpirationInterceptor)
        .addInterceptor(loginInterceptor)
//        .sslSocketFactory(clientTrustManager.)
        .build()
}