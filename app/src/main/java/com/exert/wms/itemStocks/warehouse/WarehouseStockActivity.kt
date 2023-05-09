package com.exert.wms.itemStocks.warehouse

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityWarehouseStockBinding
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.mvvmbase.BaseActivity
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*

class WarehouseStockActivity :
    BaseActivity<ItemStocksViewModel, ActivityWarehouseStockBinding>() {

    override fun getLayoutID(): Int = R.layout.activity_warehouse_stock

    override val showHomeButton: Int = -1

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
        observeViewModel()
    }

    private fun observeViewModel() {
        mViewModel.apiAccessStatus.observe(this, Observer {
//            if (it) {
//            launchLoginScreen()
//            } else {
//                showBriefToastMessage(getString(R.string.error_login_message), coordinateLayout)
//            }
        })

        mViewModel.errorApiAccessMessage.observe(this, Observer { status ->
//            showBriefToastMessage(status, coordinateLayout)
//            launchLoginScreen()
        })
    }

    override fun onBindData(binding: ActivityWarehouseStockBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}