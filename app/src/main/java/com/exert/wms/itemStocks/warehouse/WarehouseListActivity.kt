package com.exert.wms.itemStocks.warehouse

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityWarehouseListBinding
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class WarehouseListActivity :
    BaseActivity<ItemStocksViewModel, ActivityWarehouseListBinding>() {

    override val title = R.string.item_stock_status
    override fun getLayoutID(): Int = R.layout.activity_warehouse_list

    override val showHomeButton: Int = 1

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }
    var itemDto: ItemsDto? = null
    var itemPartCode: String? = ""
    var itemSerialNo: String? = ""

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        itemDto = intent.getSerializable(Constants.ITEM_DTO, ItemsDto::class.java)
        itemPartCode = intent.getStringExtra(Constants.ITEM_PART_CODE)
        itemSerialNo = intent.getStringExtra(Constants.ITEM_SERIAL_NO)

        itemDto?.let { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
            mViewModel.setItemDto(dto)
            if (dto.wStockDetails != null) {
                binding.warehouseListRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.warehouseListRecyclerView.adapter =
                    WarehouseListAdapter(dto.wStockDetails, dto.IsSerialItem) {
                        navigateToWarehouse(it)
                    }
            }

        }
        mViewModel.getItemsStatus.observe(this, Observer {
            if (!it) {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

        mViewModel.errorGetItemsStatusMessage.observe(this, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
        })

        mViewModel.itemDto.observe(this, Observer { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        })
    }

    override fun onBindData(binding: ActivityWarehouseListBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

    private fun navigateToWarehouse(warehouse: WarehouseStockDetails) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
        bundle.putSerializable(Constants.WAREHOUSE_STOCK_DETAILS, warehouse)
        bundle.putString(Constants.ITEM_PART_CODE,itemPartCode)
        bundle.putString(Constants.ITEM_SERIAL_NO,itemSerialNo)
        startActivity<SerialNumbersListActivity>(bundle)
    }
}