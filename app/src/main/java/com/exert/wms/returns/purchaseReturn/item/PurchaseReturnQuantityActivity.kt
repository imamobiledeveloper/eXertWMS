package com.exert.wms.returns.purchaseReturn.item

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
import com.exert.wms.databinding.ActivityPurchaseReturnQuantityBinding
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.addItem.AddStockItemDialogFragment
import com.exert.wms.stockAdjustment.item.OnItemAddListener
import com.exert.wms.stockAdjustment.item.OnItemCheckListener
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PurchaseReturnQuantityActivity :
    BaseActivity<PurchaseReturnItemViewModel, ActivityPurchaseReturnQuantityBinding>() {

    override val title = R.string.item_stock_status

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_purchase_return_quantity

    override val mViewModel by lazy {
        getViewModel<PurchaseReturnItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var itemDto: ItemsDto? = null
    var serialItemsList: SerialItemsDtoList? = null
    var adjustmentType: String = ""
    private var warehouseStockDetails: WarehouseStockDetails? = null
    private val checkedItems: ArrayList<SerialItemsDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseReturnQuantityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        adjustmentType = intent.getStringExtra(Constants.ADJUSTMENT_TYPE).toString()
        itemDto = intent.getSerializable(Constants.ITEM_DTO, ItemsDto::class.java)
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
        mViewModel.setWarehouseAndItemDetails(
            itemDto,
            warehouseStockDetails,
            adjustmentType,
            serialItemsList
        )

        itemDto?.let { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }
        warehouseStockDetails?.let {
            binding.itemNameManufactureLayout.itemManufactureEditText.text = it.WarehouseDescription
        }

        binding.saveButton.setOnClickListener {
            itemDto?.ItemID?.let { it1 -> mViewModel.getSelectedItems(it1) }
        }

//        binding.addButton.setOnClickListener {
//            showBottomSheetDialog()
//        }

        mViewModel.errorGetItemsStatusMessage.observe(this) { status ->
            showBriefToastMessage(status, coordinateLayout)
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
                                checkedItems.remove(item)
                                mViewModel.setCheckedItems(checkedItems)
                                mViewModel.checkAndEnableSaveButton()
                            }

                        })
            }
        }

//        mViewModel.showAddItemButton.observe(this, Observer {
//            binding.addButton.visibility = if (it) View.VISIBLE else View.INVISIBLE
//        })

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

    override fun onBindData(binding: ActivityPurchaseReturnQuantityBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}