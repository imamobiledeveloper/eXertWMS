package com.exert.wms.mvvmbase.modules

import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.login.api.LoginRepository
import com.exert.wms.stockAdjustment.api.StockAdjustmentRepository
import com.exert.wms.stockReconciliation.api.StockReconciliationRepository
import com.exert.wms.transferIn.api.TransferInRepository
import com.exert.wms.transferOut.api.TransferOutRepository
import com.exert.wms.warehouse.WarehouseRepository
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
    single {
        WarehouseRepository(get())
    }
    single {
        StockReconciliationRepository(get())
    }
    single {
        TransferOutRepository(get())
    }
    single {
        TransferInRepository(get())
    }
}