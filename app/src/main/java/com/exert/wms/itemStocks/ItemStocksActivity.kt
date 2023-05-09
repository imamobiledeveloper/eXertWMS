package com.exert.wms.itemStocks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityItemStocksBinding
import com.exert.wms.home.HomeViewModel
import com.exert.wms.mvvmbase.BaseActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ItemStocksActivity : BaseActivity<ItemStocksViewModel, ActivityItemStocksBinding>() {

    override val title = R.string.item_stocks

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_item_stocks

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun onBindData(binding: ActivityItemStocksBinding) {
        binding.viewModel = mViewModel
    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()

    }

    private fun observeViewModel() {

    }

}