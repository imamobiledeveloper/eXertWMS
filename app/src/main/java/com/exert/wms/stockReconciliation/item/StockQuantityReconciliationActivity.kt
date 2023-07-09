package com.exert.wms.stockReconciliation.item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.databinding.ActivityStockQuantityReconciliationBinding
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.item.AddStockItemDialogFragment
import com.exert.wms.stockAdjustment.item.OnItemAddListener
import com.exert.wms.stockAdjustment.item.OnItemCheckListener
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockQuantityReconciliationActivity :
    BaseActivity<StockItemReconciliationViewModel, ActivityStockQuantityReconciliationBinding>() {

    override val title = R.string.quantity_reconciliation

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_stock_quantity_reconciliation

    override val mViewModel by lazy {
        getViewModel<StockItemReconciliationViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var itemDto: ItemsDto? = null
    var quantity: String = ""
    private var warehouseStockDetails: WarehouseStockDetails? = null
    private val checkedItems: ArrayList<SerialItemsDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockQuantityReconciliationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        quantity = intent.getStringExtra(Constants.ITEM_QUANTITY).toString()
        itemDto = intent.getSerializable(Constants.ITEM_DTO, ItemsDto::class.java)
        warehouseStockDetails =
            intent.getSerializable(
                Constants.WAREHOUSE_STOCK_DETAILS,
                WarehouseStockDetails::class.java
            )
        mViewModel.setWarehouseAndItemDetails(itemDto, warehouseStockDetails, quantity)

        itemDto?.let { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }
        warehouseStockDetails?.let {
            binding.itemNameManufactureLayout.itemManufactureEditText.text = it.WarehouseDescription
        }

        binding.saveButton.setOnClickListener {
            mViewModel.getSelectedItems()
        }

        binding.addButton.setOnClickListener {
            mViewModel.checkAddedItemsCount()
        }

        mViewModel.errorGetItemsStatusMessage.observe(this, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
        })

        mViewModel.warehouseSerialNosList.observe(this, Observer { list ->
            if (list != null) {
                binding.serialNumbersListRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.serialNumbersListRecyclerView.adapter =
                    SerialNumbersListAdapter(
                        list,
                        mViewModel.getCheckBoxStateValue(),
                        object : OnItemCheckListener {
                            override fun onItemCheck(item: SerialItemsDto) {
                                if (mViewModel.checkNoOfItemsToSelect()) {
                                    checkedItems.add(item)
                                    mViewModel.setCheckedItems(checkedItems)
                                } else {
                                    showBriefToastMessage(
                                        getString(R.string.error_stock_reconciliation_add_more),
                                        coordinateLayout
                                    )
                                }
                            }

                            override fun onItemUncheck(item: SerialItemsDto) {
                                checkedItems.remove(item)
                                mViewModel.setCheckedItems(checkedItems)
                            }

                        })
            }
        })

        mViewModel.showAddItemButton.observe(this, Observer {
            binding.addButton.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        mViewModel.enableSaveButton.observe(this, Observer {
            binding.saveButton.isEnabled = it
        })

        mViewModel.showDialogToAddItem.observe(this, Observer {
            if (it) {
                showBottomSheetDialog()
            } else {
                showBriefToastMessage(
                    getString(R.string.error_stock_reconciliation_add_more),
                    coordinateLayout
                )
            }
        })

        mViewModel.checkedSerialItemsList.observe(this, Observer { list ->
            val data = Intent()
            data.putParcelableArrayListExtra(Constants.CHECKED_SERIAL_ITEMS, list)
            setResult(Activity.RESULT_OK, data)
            finish()
        })

    }

    private fun showBottomSheetDialog() {
        val dialog = AddStockItemDialogFragment(object : OnItemAddListener {
            override fun onAddItem(item: SerialItemsDto) {
                if (item?.ManufactureDate != null) {
                    checkedItems.add(item)
                    mViewModel.setCheckedItems(checkedItems)
                }
            }

        })
        dialog.show(this.supportFragmentManager, "AddStockItemDialogFragment")
    }

    override fun onBindData(binding: ActivityStockQuantityReconciliationBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}