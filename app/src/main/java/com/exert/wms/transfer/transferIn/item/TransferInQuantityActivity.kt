package com.exert.wms.transfer.transferIn.item

import android.os.Build
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.databinding.ActivityTransferInQuantityBinding
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.item.OnItemCheckListener
import com.exert.wms.transfer.api.ExternalTransferItemsDto
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransferInQuantityActivity :
    BaseActivity<TransferInItemViewModel, ActivityTransferInQuantityBinding>() {

    override val title = R.string.item_stock_status

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_transfer_out_quantity

    override val mViewModel by lazy {
        getViewModel<TransferInItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var tItemDto: ExternalTransferItemsDto? = null
    var serialItemsList: SerialItemsDtoList? = null
    private var warehouseStockDetails: WarehouseStockDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferInQuantityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        tItemDto = intent.getSerializable(Constants.ITEM_DTO, ExternalTransferItemsDto::class.java)
        serialItemsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                Constants.USER_SELECTED_WAREHOUSE_LIST,
                SerialItemsDtoList::class.java
            )
        } else {
            intent.getParcelableExtra(Constants.USER_SELECTED_WAREHOUSE_LIST)
        }
        warehouseStockDetails =
            intent.getSerializable(
                Constants.WAREHOUSE_STOCK_DETAILS,
                WarehouseStockDetails::class.java
            )
        mViewModel.setSelectedExternalTransferItemsDto(tItemDto)
        warehouseStockDetails?.let {
            binding.itemNameManufactureLayout.itemManufactureEditText.text = it.WarehouseDescription
        }
        mViewModel.transferInSerialItems.observe(this) { list ->
            if (list != null) {
                binding.serialNumbersListRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.serialNumbersListRecyclerView.adapter =
                    SerialNumbersListAdapter(
                        list,
                        checkBoxState = false,
                        object : OnItemCheckListener {
                            override fun onItemCheck(item: SerialItemsDto) {}
                            override fun onItemUncheck(item: SerialItemsDto) {}
                        })
            }
        }

        mViewModel.errorFieldMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        }

        mViewModel.convertedItemsDto.observe(this) { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }
    }

    override fun onBindData(binding: ActivityTransferInQuantityBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}