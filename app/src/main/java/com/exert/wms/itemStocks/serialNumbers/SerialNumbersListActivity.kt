package com.exert.wms.itemStocks.serialNumbers

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivitySerialNumbersListBinding
import com.exert.wms.itemStocks.ItemStocksViewModel
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SerialNumbersListActivity :
    BaseActivity<ItemStocksViewModel, ActivitySerialNumbersListBinding>() {

    override val title = R.string.warehouse_status
    override fun getLayoutID(): Int = R.layout.activity_serial_numbers_list

    override val showHomeButton: Int = 1

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    val checkBoxState =
        if (intent !== null && intent.hasExtra(SHOW_CHECKBOX)) intent.getBooleanExtra(
            SHOW_CHECKBOX,
            false
        ) else false

    var itemDto: ItemsDto? = null

    var warehouseStockDetails: WarehouseStockDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySerialNumbersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        itemDto = intent.getSerializable(Constants.ITEM_DTO, ItemsDto::class.java)
        warehouseStockDetails =
            intent.getSerializable(Constants.ITEM_WAREHOUSE, WarehouseStockDetails::class.java)
        mViewModel.getSerialNumbersList(itemDto, warehouseStockDetails)
        itemDto?.let { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }
        warehouseStockDetails?.let {
            binding.itemNameManufactureLayout.itemManufactureEditText.text = it.WarehouseDescription
        }

        mViewModel.setCheckBoxState(checkBoxState)
        mViewModel.getItemsSerialNosStatus.observe(this, Observer {
            if (!it) {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

        mViewModel.errorGetItemsStatusMessage.observe(this, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
        })

        mViewModel.warehouseSerialIsList.observe(this, Observer { list ->
            binding.serialNumbersListRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.serialNumbersListRecyclerView.adapter =
                SerialNumbersListAdapter(list, checkBoxState)
        })

    }

    override fun onBindData(binding: ActivitySerialNumbersListBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

    companion object {
        private const val SHOW_CHECKBOX: String = "SHOW_CHECKBOX"
    }
}