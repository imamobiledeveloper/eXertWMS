package com.exert.wms.mvvmbase.modules

import com.exert.wms.itemStocks.api.ItemStocksDataSource
import com.exert.wms.itemStocks.api.ItemStocksDataSourceLocal
import com.exert.wms.itemStocks.api.ItemStocksDataSourceRemote
import com.exert.wms.login.api.LoginDataSource
import com.exert.wms.login.api.LoginDataSourceLocal
import com.exert.wms.login.api.LoginDataSourceRemote
import com.exert.wms.stockAdjustment.api.StockAdjustmentDataSource
import com.exert.wms.stockAdjustment.api.StockAdjustmentDataSourceRemote
import com.exert.wms.stockReconciliation.api.StockReconciliationDataSource
import com.exert.wms.stockReconciliation.api.StockReconciliationDataSourceRemote
import com.exert.wms.transfer.api.TransferDataSource
import com.exert.wms.transfer.api.TransferDataSourceRemote
import com.exert.wms.warehouse.WarehouseDataSource
import com.exert.wms.warehouse.WarehouseDataSourceRemote
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
            exertWmsApi = get(named("exertTokenApi"))
        )
    }

    single {
        ItemStocksDataSourceLocal()
    }

    single {
        ItemStocksDataSource(itemStocksDataSourceRemote = get(), itemStocksDataSourceLocal = get())
    }

    factory {
        StockAdjustmentDataSourceRemote(
            exertWmsApi = get(named("exertTokenApi"))
        )
    }

    single {
        StockAdjustmentDataSource(stockAdjustmentDataSourceRemote = get())
    }

    single {
        WarehouseDataSource( get())
    }

    factory {
        WarehouseDataSourceRemote(
            exertWmsApi = get(named("exertTokenApi"))
        )
    }

    single {
        StockReconciliationDataSource( get())
    }

    factory {
        StockReconciliationDataSourceRemote(
            exertWmsApi = get(named("exertTokenApi"))
        )
    }

    single {
        TransferDataSource( get())
    }

    factory {
        TransferDataSourceRemote(
            exertWmsApi = get(named("exertTokenApi"))
        )
    }
}