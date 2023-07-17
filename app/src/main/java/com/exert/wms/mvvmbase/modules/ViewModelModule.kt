package com.exert.wms.mvvmbase.modules

import com.exert.wms.deliveryNote.DeliveryNoteBaseViewModel
import com.exert.wms.deliveryReceipt.DeliveryReceiptBaseViewModel
import com.exert.wms.home.HomeViewModel
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.login.LoginViewModel
import com.exert.wms.purchaseReturn.PurchaseReturnBaseViewModel
import com.exert.wms.salesReturn.SalesReturnBaseViewModel
import com.exert.wms.splash.SplashViewModel
import com.exert.wms.stockAdjustment.StockAdjustmentBaseViewModel
import com.exert.wms.stockAdjustment.item.AddStockItemViewModel
import com.exert.wms.stockAdjustment.item.StockItemAdjustmentViewModel
import com.exert.wms.stockReconciliation.StockReconciliationBaseViewModel
import com.exert.wms.stockReconciliation.item.StockItemReconciliationViewModel
import com.exert.wms.transferIn.TransferInBaseViewModel
import com.exert.wms.transferOut.TransferOutBaseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SplashViewModel()
    }

    viewModel {
        LoginViewModel(get(), get(), get())
    }

    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        ItemStocksViewModel(get(), get())
    }

    viewModel {
        StockAdjustmentBaseViewModel(get(), get(), get())
    }

    viewModel {
        StockItemAdjustmentViewModel( get(), get())
    }

    viewModel {
        AddStockItemViewModel(get())
    }

    viewModel {
        StockReconciliationBaseViewModel(get(), get(), get())
    }

    viewModel {
        StockItemReconciliationViewModel(get(), get(), get())
    }
    viewModel {
        TransferOutBaseViewModel(get(), get(), get())
    }
    viewModel {
        TransferInBaseViewModel(get(), get(), get())
    }
    viewModel {
        DeliveryReceiptBaseViewModel(get(), get(), get())
    }
    viewModel {
        DeliveryNoteBaseViewModel(get(), get(), get())
    }
    viewModel {
        PurchaseReturnBaseViewModel(get(), get(), get())
    }
    viewModel {
        SalesReturnBaseViewModel(get(), get(), get())
    }
}