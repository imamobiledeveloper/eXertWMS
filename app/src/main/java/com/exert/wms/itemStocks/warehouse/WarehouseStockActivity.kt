package com.exert.wms.itemStocks.warehouse

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityWarehouseStockBinding
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.mvvmbase.BaseActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class WarehouseStockActivity :
    BaseActivity<ItemStocksViewModel, ActivityWarehouseStockBinding>() {

    override val title = R.string.warehouse_status
    override fun getLayoutID(): Int = R.layout.activity_warehouse_stock

    override val showHomeButton: Int = 1

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {

    }

    override fun onBindData(binding: ActivityWarehouseStockBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}