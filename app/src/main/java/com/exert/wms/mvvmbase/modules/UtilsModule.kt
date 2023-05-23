package com.exert.wms.mvvmbase.modules

import com.exert.wms.LogoutManager
import com.exert.wms.utils.StringProvider
import com.exert.wms.utils.UserDefaults
import com.exert.wms.mvvmbase.network.AuthInterceptor
import com.exert.wms.mvvmbase.network.SessionExpirationInterceptor
import com.exert.wms.mvvmbase.network.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module(override = true) {
    factory {
        StringProvider(androidContext())
    }
    single {
        LogoutManager(get())
    }
    single {
        UserDefaults(get())
    }
    single {
        AuthInterceptor(get())
    }
    single {
        SessionExpirationInterceptor(get())
    }
    single {
        SessionManager()
    }
}