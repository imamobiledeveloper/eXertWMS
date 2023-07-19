package com.exert.wms.returns.purchaseReturn

import com.exert.wms.itemStocks.api.ItemStocksRepository
import com.exert.wms.mvvmbase.BaseViewModel
import com.exert.wms.stockAdjustment.api.StockAdjustmentRepository
import com.exert.wms.utils.StringProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PurchaseReturnBaseViewModel(
    private val stringProvider: StringProvider,
    private val itemStocksRepo: ItemStocksRepository,
    private val stockAdjustmentRepo: StockAdjustmentRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel() {
}