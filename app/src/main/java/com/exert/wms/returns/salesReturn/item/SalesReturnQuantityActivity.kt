package com.exert.wms.returns.salesReturn.item

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.databinding.ActivitySalesReturnQuantityBinding
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.returns.api.SalesItemsDetailsDto
import com.exert.wms.stockAdjustment.item.OnItemCheckListener
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SalesReturnQuantityActivity :
    BaseActivity<SalesReturnItemViewModel, ActivitySalesReturnQuantityBinding>() {

    override val title = R.string.item_stock_status

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_sales_return_quantity

    override val mViewModel by lazy {
        getViewModel<SalesReturnItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var serialItemsList: SerialItemsDtoList? = null
    private val checkedItems: ArrayList<SerialItemsDto> = ArrayList()
    var prItemDto: SalesItemsDetailsDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesReturnQuantityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()

        binding.saveButton.setOnClickListener {
            prItemDto?.ItemID?.let { it1 -> mViewModel.getSelectedItems(it1) }
        }
    }

    private fun observeViewModel() {
        prItemDto =
            intent.getSerializable(Constants.ITEM_DTO, SalesItemsDetailsDto::class.java)
        serialItemsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                Constants.USER_SELECTED_WAREHOUSE_LIST,
                SerialItemsDtoList::class.java
            )
        } else {
            intent.getParcelableExtra(Constants.USER_SELECTED_WAREHOUSE_LIST)
        }
        mViewModel.setSelectedSalesReturnItemDto(
            prItemDto,
            serialItemsList
        )
        serialItemsList?.serialItemsDto?.let { checkedItems.addAll(it) }
        mViewModel.setUserSelectedItems(checkedItems)

        mViewModel.convertedItemsDto.observe(this) { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }

        mViewModel.isLoadingData.observe(this) { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        }
        mViewModel.warehouseSerialNosList.observe(this) { list ->
            if (list != null) {
                binding.serialNumbersListRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.serialNumbersListRecyclerView.adapter =
                    SerialNumbersListAdapter(
                        list,
                        mViewModel.getCheckBoxStateValue(),
                        object : OnItemCheckListener {
                            override fun onItemCheck(item: SerialItemsDto) {
                                checkedItems.add(item)
                                mViewModel.setCheckedItems(checkedItems)
                            }

                            override fun onItemUncheck(item: SerialItemsDto) {
                                checkedItems.removeIf { it.SerialNumber == item.SerialNumber }
                                mViewModel.setCheckedItems(checkedItems)
                            }

                        })
            }
        }

        mViewModel.enableSaveButton.observe(this) {
            binding.saveButton.isEnabled = it
        }

        mViewModel.checkedSerialItemsList.observe(this) { list ->
            val data = Intent()
            data.putExtra(Constants.CHECKED_SERIAL_ITEMS, list)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        mViewModel.errorFieldMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        }
    }

    override fun onBackPressed() {
        mViewModel.alreadySelected = false
        super.onBackPressed()
    }

    override fun onBindData(binding: ActivitySalesReturnQuantityBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}