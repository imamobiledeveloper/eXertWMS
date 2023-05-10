package com.exert.wms.mvvmbase.modules

import com.exert.wms.itemStocks.api.ItemStocksDataSource
import com.exert.wms.itemStocks.api.ItemStocksDataSourceRemote
import com.exert.wms.login.api.LoginDataSource
import com.exert.wms.login.api.LoginDataSourceLocal
import com.exert.wms.login.api.LoginDataSourceRemote
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
        LoginDataSource(loginDataSourceRemote = get(), loginDataSourceLocal = get())
    }

    factory {
        ItemStocksDataSourceRemote(
            exertWmsApi = get(named("exertApi"))
        )
    }

    single {
        ItemStocksDataSource(itemStocksDataSourceRemote = get())
    }
}