package com.exert.wms.mvvmbase.modules

import com.exert.wms.delivery.deliveryNote.DeliveryNoteBaseViewModel
import com.exert.wms.delivery.deliveryNote.item.DeliveryNoteItemViewModel
import com.exert.wms.delivery.deliveryReceipt.DeliveryReceiptBaseViewModel
import com.exert.wms.delivery.deliveryReceipt.item.DeliveryReceiptItemViewModel
import com.exert.wms.home.HomeViewModel
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.login.LoginViewModel
import com.exert.wms.returns.purchaseReturn.PurchaseReturnBaseViewModel
import com.exert.wms.returns.purchaseReturn.item.PurchaseReturnItemViewModel
import com.exert.wms.returns.salesReturn.SalesReturnBaseViewModel
import com.exert.wms.returns.salesReturn.item.SalesReturnItemViewModel
import com.exert.wms.splash.SplashViewModel
import com.exert.wms.stockAdjustment.StockAdjustmentBaseViewModel
import com.exert.wms.addItem.AddStockItemViewModel
import com.exert.wms.stockAdjustment.item.StockAdjustmentItemViewModel
import com.exert.wms.stockReconciliation.StockReconciliationBaseViewModel
import com.exert.wms.stockReconciliation.item.StockReconciliationItemViewModel
import com.exert.wms.transfer.transferIn.TransferInBaseViewModel
import com.exert.wms.transfer.transferIn.item.TransferInItemViewModel
import com.exert.wms.transfer.transferOut.TransferOutBaseViewModel
import com.exert.wms.transfer.transferOut.item.TransferOutItemViewModel
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
        StockAdjustmentItemViewModel( get(), get())
    }

    viewModel {
        AddStockItemViewModel(get())
    }

    viewModel {
        StockReconciliationBaseViewModel(get(), get(), get())
    }

    viewModel {
        StockReconciliationItemViewModel(get(), get(), get())
    }
    viewModel {
        TransferOutBaseViewModel(get(), get(), get())
    }
    viewModel {
        TransferOutItemViewModel(get(), get())
    }
    viewModel {
        TransferInBaseViewModel(get(), get(), get())
    }
    viewModel {
        TransferInItemViewModel()
    }
    viewModel {
        DeliveryReceiptBaseViewModel(get(), get(), get())
    }
    viewModel {
        DeliveryReceiptItemViewModel(get(), get())
    }
    viewModel {
        DeliveryNoteBaseViewModel(get(), get(), get())
    }
    viewModel {
        DeliveryNoteItemViewModel()
    }
    viewModel {
        PurchaseReturnBaseViewModel(get(), get(), get())
    }
    viewModel {
        PurchaseReturnItemViewModel(get())
    }
    viewModel {
        SalesReturnBaseViewModel(get(), get(), get())
    }
    viewModel {
        SalesReturnItemViewModel(get())
    }
}