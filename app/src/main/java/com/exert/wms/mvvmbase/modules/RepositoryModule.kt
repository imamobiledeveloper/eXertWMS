package com.exert.wms.mvvmbase.modules

import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.login.api.LoginRepository
import com.exert.wms.stockAdjustment.api.StockAdjustmentRepository
import org.koin.dsl.module

val repositoryModule = module(override = true) {

    single {
        LoginRepository(loginDatasource = get())
    }
    single {
        ItemStocksRepository(itemStocksDataSource = get())
    }
    single {
        StockAdjustmentRepository(stockAdjustmentDataSource = get())
    }
}