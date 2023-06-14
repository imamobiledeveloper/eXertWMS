package com.exert.wms.stockAdjustment.item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityStockItemAdjustmentBinding
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.StockAdjustmentBaseViewModel
import com.exert.wms.stockAdjustment.api.WarehouseDto
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockItemAdjustmentActivity :
    BaseActivity<StockAdjustmentBaseViewModel, ActivityStockItemAdjustmentBinding>() {

    override val title = R.string.item_adjustment

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_stock_item_adjustment

    override val mViewModel by lazy {
        getViewModel<StockAdjustmentBaseViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var warehouseDto: WarehouseDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockItemAdjustmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout.setStartIconOnClickListener {
            showBriefToastMessage(
                "Clicked Item Part code",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
        }
        binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout.setStartIconOnClickListener {
            showBriefToastMessage(
                "Clicked Item Serial number",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
        }
        binding.adjustmentQuantityEditTextLayout.setEndIconOnClickListener {
//            mViewModel.checkItemDetailsEntered(
////                binding.warehouseSpinnerTV.text.toString(),
//                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString(),
//                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString(),
//            )
//            val intent = Intent(this, SerialNumbersListActivity::class.java)
//            intent.putExtra(SHOW_CHECKBOX, true)
//            startForResult.launch(intent)

            val bundle = Bundle()
            bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
            bundle.putSerializable(Constants.ITEM_WAREHOUSE, warehouseDto)
            startActivity<SerialNumbersListActivity>(bundle)
        }
    }

    private fun observeViewModel() {
        warehouseDto = intent.getSerializableExtra(Constants.WAREHOUSE) as WarehouseDto?
        mViewModel.setSelectedWarehouseDto(warehouseDto)
        binding.saveButton.setOnClickListener {
            mViewModel.saveItemStock(
//                binding.warehouseSpinnerTV.text.toString(),
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString(),
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString(),
            )
        }
        binding.itemPartCodeSerialNoLayout.searchItemPartCodeIV.setOnClickListener {
            mViewModel.searchItemWithPartCode(binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString())
        }
        binding.itemPartCodeSerialNoLayout.searchItemSerialNoIV.setOnClickListener {
            mViewModel.searchItemWithSerialNumber(binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString())
        }
        binding.radioButtonParent.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            mViewModel.setAdjustmentType(radioButton.text as String)
        }
        mViewModel.isLoadingData.observe(this, Observer { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        })

        mViewModel.errorFieldMessage.observe(this, Observer { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        })

        mViewModel.errorItemSelectionMessage.observe(this, Observer {
            if (it) {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                )
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                )
            } else {
                enableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                    getString(R.string.error_item_partcode_serial_no_empty_message)
                )
            }
        })

        mViewModel.saveItemStatus.observe(this, Observer {
            if (it) {
                showBriefToastMessage(getString(R.string.item_saved_message), coordinateLayout)
                finish()
            } else {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

        mViewModel.navigateToSerialNo.observe(this, Observer {
            if (it) {
//                val intent = Intent(this, SerialNumbersListActivity::class.java)
//                intent.putExtra(SHOW_CHECKBOX, true)
//                startForResult.launch(intent)
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                bundle.putSerializable(Constants.ITEM_WAREHOUSE, warehouseDto)
                startActivity<SerialNumbersListActivity>(bundle)
            } else {
                showBriefToastMessage(getString(R.string.invalid_details_message), coordinateLayout)
            }
        })
        mViewModel.enableSaveButton.observe(this, Observer {
            binding.saveButton.isEnabled=it
        })

        mViewModel.errorItemPartCode.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                    getString(R.string.error_item_partcode_serial_no_empty_message)
                )
            } else {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                )
            }
        })
        mViewModel.errorItemSerialNo.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                    getString(R.string.error_item_partcode_serial_no_empty_message)
                )
            } else {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                )
            }
        })
        mViewModel.itemDto.observe(this, Observer { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        })

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                //do stuff here
            }
        }

    override fun onBindData(binding: ActivityStockItemAdjustmentBinding) {
        binding.viewModel = mViewModel
    }

    companion object {
        private const val SHOW_CHECKBOX: String = "SHOW_CHECKBOX"
        private const val SERIAL_NO_ACTIVITY_REQUEST_CODE: Int = 1
    }
}