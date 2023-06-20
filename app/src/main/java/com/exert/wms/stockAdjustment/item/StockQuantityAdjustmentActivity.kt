package com.exert.wms.stockAdjustment.item

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityStockQuantityAdjustmentBinding
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.StockAdjustmentBaseViewModel
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockQuantityAdjustmentActivity :
    BaseActivity<StockAdjustmentBaseViewModel, ActivityStockQuantityAdjustmentBinding>() {

    override val title = R.string.quantity_adjustment

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_stock_quantity_adjustment

    override val mViewModel by lazy {
        getViewModel<StockAdjustmentBaseViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var itemDto: ItemsDto? = null
    var adjustmentType: String = ""
    var warehouseStockDetails: WarehouseStockDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockQuantityAdjustmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        adjustmentType = intent.getStringExtra(Constants.ADJUSTMENT_TYPE).toString()
        itemDto = intent.getSerializable(Constants.ITEM_DTO, ItemsDto::class.java)
        warehouseStockDetails =
            intent.getSerializable(
                Constants.WAREHOUSE_STOCK_DETAILS,
                WarehouseStockDetails::class.java
            )
        mViewModel.setWarehouseAndItemDetails(itemDto, warehouseStockDetails, adjustmentType)

        itemDto?.let { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }
        warehouseStockDetails?.let {
            binding.itemNameManufactureLayout.itemManufactureEditText.text = it.WarehouseDescription
        }

        mViewModel.errorGetItemsStatusMessage.observe(this, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
        })

        mViewModel.warehouseSerialNosList.observe(this, Observer { list ->
            if (list != null) {
                binding.serialNumbersListRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.serialNumbersListRecyclerView.adapter =
                    SerialNumbersListAdapter(list, mViewModel.getCheckBoxStateValue())
            }
        })

    }

    override fun onBindData(binding: ActivityStockQuantityAdjustmentBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

    companion object {
        private const val SHOW_CHECKBOX: String = "SHOW_CHECKBOX"
    }
}