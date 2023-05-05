package com.exert.wms.mvvmbase.modules

import com.exert.wms.login.LoginDataSource
import com.exert.wms.login.LoginDataSourceLocal
import com.exert.wms.login.LoginDataSourceRemote
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cacheModule = module {

    factory {
        LoginDataSourceRemote(
            exertWmsApi = get(named("exertApi"))
        )
    }

    factory {
        LoginDataSourceLocal()
    }

    single {
        LoginDataSource(get(), get())
    }
}