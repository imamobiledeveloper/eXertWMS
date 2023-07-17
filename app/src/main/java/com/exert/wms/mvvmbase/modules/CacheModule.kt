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
import com.exert.wms.transferIn.api.TransferInDataSource
import com.exert.wms.transferIn.api.TransferInDataSourceRemote
import com.exert.wms.transferOut.api.TransferOutDataSource
import com.exert.wms.transferOut.api.TransferOutDataSourceRemote
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
        TransferInDataSource( get())
    }

    factory {
        TransferInDataSourceRemote(
            exertWmsApi = get(named("exertTokenApi"))
        )
    }

    single {
        TransferOutDataSource( get())
    }

    factory {
        TransferOutDataSourceRemote(
            exertWmsApi = get(named("exertTokenApi"))
        )
    }
}