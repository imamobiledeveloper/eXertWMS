package com.exert.wms.stockAdjustment.item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityStockItemAdjustmentBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.StockAdjustmentBaseViewModel
import com.exert.wms.stockAdjustment.api.SerialItemsDto
import com.exert.wms.stockAdjustment.api.WarehouseDto
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
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
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout.setEndIconOnClickListener {
            mViewModel.searchItemWithPartCode(binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString())
        }
        binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout.setEndIconOnClickListener {
            mViewModel.searchItemWithSerialNumber(binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString())
        }
        binding.adjustmentQuantityEditTextLayout.setEndIconOnClickListener {
            mViewModel.checkItemDetailsEntered(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString(),
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString(),
            )
        }
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextViewText(
                        binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV,
                        "",
                        View.GONE
                    )
                } else {
                    if (binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.isNullOrEmpty()) {
                        setTextViewText(
                            binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV,
                            getString(R.string.item_part_code_hint),
                            View.VISIBLE
                        )
                    } else {
                        setTextViewText(
                            binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV,
                            "",
                            View.GONE
                        )
                    }
                }
            }

        binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextViewText(
                        binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV,
                        "",
                        View.GONE
                    )
                } else {
                    if (binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.isNullOrEmpty()) {
                        setTextViewText(
                            binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV,
                            getString(R.string.item_serial_no_hint),
                            View.VISIBLE
                        )
                    } else {
                        setTextViewText(
                            binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV,
                            "",
                            View.GONE
                        )
                    }
                }
            }
    }

    private fun observeViewModel() {
        warehouseDto = intent.getSerializable(Constants.WAREHOUSE, WarehouseDto::class.java)
        mViewModel.setSelectedWarehouseDto(warehouseDto)

        binding.saveButton.setOnClickListener {
            mViewModel.saveItemStock(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString(),
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString(),
            )
        }
        binding.itemPartCodeSerialNoLayout.scanItemPartCodeIV.setOnClickListener {
            showBriefToastMessage(
                "Clicked Item Part code",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
        }
        binding.itemPartCodeSerialNoLayout.scanItemSerialNoIV.setOnClickListener {
            showBriefToastMessage(
                "Clicked Item Serial number",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
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
                showBriefToastMessage(
                    getString(R.string.item_saved_message), coordinateLayout,
                    getColor(R.color.blue_50)
                )

                val intent = Intent().apply {
                    putExtra(Constants.STOCK_ITEMS_DETAILS_DTO, mViewModel.getSavedItemDto())
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

        mViewModel.navigateToSerialNo.observe(this, Observer {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                bundle.putSerializable(
                    Constants.WAREHOUSE_STOCK_DETAILS,
                    mViewModel.getWarehouseStockDetails()
                )
                bundle.putString(Constants.ADJUSTMENT_TYPE, mViewModel.getAdjustmentType())
                val intent = Intent(this, StockQuantityAdjustmentActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)

            } else {
                showBriefToastMessage(getString(R.string.invalid_details_message), coordinateLayout)
            }
        })
        mViewModel.enableSaveButton.observe(this, Observer {
            binding.saveButton.isEnabled = it
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

        mViewModel.isItemSerialized.observe(this, Observer { isItSerialized ->
            binding.adjustmentQuantityEditText.isEnabled = !isItSerialized
        })

        mViewModel.adjustmentQuantityString.observe(this, Observer { value ->
            binding.adjustmentQuantityEditText.text = value.toEditable()
        })

        mViewModel.adjustmentTotalCostString.observe(this, Observer { value ->
            binding.adjustmentTotalCostEditText.text = value.toEditable()
        })

        mViewModel.costString.observe(this, Observer { value ->
            binding.costEditText.text = value.toEditable()
        })

        mViewModel.errorAdjustmentType.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.adjustmentQuantityEditTextLayout,
                    binding.adjustmentQuantityEditText,
                    getString(R.string.adjustment_type_empty_message), false
                )
            } else {
                disableErrorMessage(
                    binding.adjustmentQuantityEditTextLayout,
                    binding.adjustmentQuantityEditText,
                )
            }
        })

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val data = it.extras
                    val serialItemsList =
                        data?.getParcelableArrayList<SerialItemsDto>(Constants.CHECKED_SERIAL_ITEMS)
                    mViewModel.setSelectedSerialItemsList(serialItemsList)
                }
            }
        }

    override fun onBindData(binding: ActivityStockItemAdjustmentBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }

    companion object {
        private const val SHOW_CHECKBOX: String = "SHOW_CHECKBOX"
        private const val SERIAL_NO_ACTIVITY_REQUEST_CODE: Int = 1
    }
}