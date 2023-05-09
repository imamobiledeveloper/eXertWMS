package com.exert.wms.itemStocks.status

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityItemStockStatusBinding
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.mvvmbase.BaseActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ItemStockStatusActivity :
    BaseActivity<ItemStocksViewModel, ActivityItemStockStatusBinding>() {

    override fun getLayoutID(): Int = R.layout.activity_item_stock_status

    override val showHomeButton: Int = -1

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemStockStatusBinding.inflate(layoutInflater)
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


    override fun onBindData(binding: ActivityItemStockStatusBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}