package com.exert.wms.stockAdjustment

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityStockAdjustmentBaseBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.item.StockItemAdjustmentActivity
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockAdjustmentBaseActivity :
    BaseActivity<StockAdjustmentBaseViewModel, ActivityStockAdjustmentBaseBinding>() {

    override val title = R.string.stock_adjustment

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_stock_adjustment_base

    override val mViewModel by lazy {
        getViewModel<StockAdjustmentBaseViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockAdjustmentBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        binding.addItemsTV.setOnClickListener {
//            showBriefToastMessage(
//                "Clicked Add Item",
//                coordinateLayout,
//                getColor(R.color.blue_50)
//            )
            startActivity<StockItemAdjustmentActivity>()
        }
        binding.updateButton.setOnClickListener {
            showBriefToastMessage(
                "Clicked save button",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
            mViewModel.saveItems()
        }
        mViewModel.isLoadingData.observe(this, Observer { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        })

        mViewModel.enableUpdateButton.observe(this, Observer {
            binding.updateButton.isEnabled = it
        })
        mViewModel.itemsList.observe(this, Observer { list ->
            binding.itemsListRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.itemsListRecyclerView.adapter = StockAdjustmentItemsListAdapter(list) {
                navigateToItemAdjustment(it)
            }
        })
    }

    override fun onBindData(binding: ActivityStockAdjustmentBaseBinding) {
        binding.viewModel = mViewModel
    }

    private fun navigateToItemAdjustment(itemName: String) {
        startActivity<StockItemAdjustmentActivity>()
    }
}