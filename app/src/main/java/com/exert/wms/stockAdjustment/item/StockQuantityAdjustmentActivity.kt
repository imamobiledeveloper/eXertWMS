package com.exert.wms.stockAdjustment.item

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityStockQuantityAdjustmentBinding
import com.exert.wms.itemStocks.api.ItemsDto
import com.exert.wms.itemStocks.api.WarehouseSerialItemDetails
import com.exert.wms.itemStocks.api.WarehouseStockDetails
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.StockAdjustmentBaseViewModel
import com.exert.wms.utils.Constants
import com.exert.wms.utils.toEditable
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*

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
    private var warehouseStockDetails: WarehouseStockDetails? = null
    private val checkedItems: ArrayList<WarehouseSerialItemDetails> = ArrayList()

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

        binding.saveButton.setOnClickListener {
            mViewModel.getSelectedItems()
        }

        binding.addButton.setOnClickListener {
            showDialog()
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
                            override fun onItemCheck(item: WarehouseSerialItemDetails) {
                                checkedItems.add(item)
                                mViewModel.setCheckedItems(checkedItems)
                            }

                            override fun onItemUncheck(item: WarehouseSerialItemDetails) {
                                checkedItems.remove(item)
                                mViewModel.setCheckedItems(checkedItems)
                            }

                        })
            }
        })

        mViewModel.enableSaveButton.observe(this, Observer {
            binding.saveButton.isEnabled = it
        })

        mViewModel.checkedSerialItemsList.observe(this, Observer { list ->
            val data = Intent()
            data.putParcelableArrayListExtra(Constants.CHECKED_SERIAL_ITEMS, list)
            setResult(Activity.RESULT_OK, data);
            finish()
        })

    }

    private fun showDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_add_stock_item_layout)

        val addButton = dialog.findViewById<Button>(R.id.addStockItemButton)
        val manufactureDateET = dialog.findViewById<EditText>(R.id.manufactureDateEditText)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)

        dialog.setCancelable(false)


        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                manufactureDateET?.text = "$dayOfMonth /$monthOfYear /$year".toEditable()
            }
        closeButton?.setOnClickListener {
            dialog.dismiss()
        }
        manufactureDateET?.setOnClickListener {
            val datePicker = DatePickerDialog(
                this@StockQuantityAdjustmentActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis();
            datePicker.show()
        }

        dialog.show()

    }

    override fun onBindData(binding: ActivityStockQuantityAdjustmentBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}