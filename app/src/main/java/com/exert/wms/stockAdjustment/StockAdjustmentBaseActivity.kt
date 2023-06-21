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
import com.exert.wms.utils.*
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
        binding.warehouseSpinner.selected { parent, position ->
            binding.warehouseSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.warehouseSpinnerTV.isActivated = true
            }
            mViewModel.selectedWarehouse(parent?.getItemAtPosition(position).toString())
            binding.warehouseSpinner.setSelection(mViewModel.getSelectedWarehouseIndex())
        }
        binding.addItemsTV.setOnClickListener {
            mViewModel.checkWarehouse()
        }
        binding.updateButton.setOnClickListener {
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
        mViewModel.errorWarehouse.observe(this, Observer {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                bundle.putSerializable(Constants.WAREHOUSE, mViewModel.getWarehouseObject())
                startActivity<StockItemAdjustmentActivity>(bundle)
            } else {
                showBriefToastMessage(
                    getString(R.string.warehouse_empty_message),
                    coordinateLayout,
                    getColor(R.color.blue_50)
                )
            }
        })
        mViewModel.warehouseList.observe(this, Observer {
//            if (it.isNotEmpty()) {
//                setWarehouseList(it)
//            }
        })
        mViewModel.warehouseStringList.observe(this, Observer {
            if (it.isNotEmpty()) {
                setWarehouseList(it)
            }
        })

    }

    private fun setWarehouseList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            this,
            stringList.toTypedArray(),
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.warehouseSpinner.adapter = adapter
    }

    override fun onBindData(binding: ActivityStockAdjustmentBaseBinding) {
        binding.viewModel = mViewModel
    }

    private fun navigateToItemAdjustment(itemName: String) {
        startActivity<StockItemAdjustmentActivity>()
    }
}