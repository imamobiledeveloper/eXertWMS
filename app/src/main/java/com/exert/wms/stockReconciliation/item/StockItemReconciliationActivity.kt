package com.exert.wms.stockReconciliation.item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.databinding.ActivityStockItemReconciliationBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.item.StockQuantityAdjustmentActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
import com.exert.wms.warehouse.WarehouseDto
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockItemReconciliationActivity :
    BaseActivity<StockItemReconciliationViewModel, ActivityStockItemReconciliationBinding>() {

    override val title = R.string.item_reconciliation

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_stock_item_reconciliation

    override val mViewModel by lazy {
        getViewModel<StockItemReconciliationViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel
    override fun onBindData(binding: ActivityStockItemReconciliationBinding) {

    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var warehouseDto: WarehouseDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockItemReconciliationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)

        addTextWatcherListeners()
        addFocusChangeListeners()
        observeViewModel()
        setItemPartCodeAndSerialNoCLickListeners()

        binding.quantityEditTextLayout.setEndIconOnClickListener {
            mViewModel.checkItemDetailsEntered(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString(),
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString(),
            )
        }

    }

    private fun setItemPartCodeAndSerialNoCLickListeners() {
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout.setEndIconOnClickListener {
            clearOtherField(
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV
            )
            mViewModel.setItemSerialNumberValue(getString(R.string.empty))
            mViewModel.setItemPartCodeValue(binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString())
            mViewModel.searchItemWithPartCode()
        }
        binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout.setEndIconOnClickListener {
            clearOtherField(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV
            )
            mViewModel.setItemPartCodeValue(getString(R.string.empty))
            mViewModel.setItemSerialNumberValue(binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString())
            mViewModel.searchItemWithSerialNumber()
        }
    }

    private fun clearOtherField(
        clearEditText: TextInputEditText,
        clearHintTV: TextView
    ) {
        hideKeyBoard()
        clearFields()
        clearTextInputEditText(clearEditText, clearHintTV)
    }

    private fun addFocusChangeListeners() {
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextViewVisibility(
                        binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV,
                        View.GONE
                    )
                } else {
                    val value =
                        binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.isNullOrEmpty()
                    setTextViewVisibility(
                        binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV,
                        if (value) {
                            View.VISIBLE
                        } else View.GONE
                    )
                }
            }

        binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextViewVisibility(
                        binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV,
                        View.GONE
                    )
                } else {
                    val value =
                        binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.isNullOrEmpty()
                    setTextViewVisibility(
                        binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV,
                        if (value) {
                            View.VISIBLE
                        } else View.GONE
                    )
                }
            }
    }

    private fun addTextWatcherListeners() {
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.addTextChangedListener(
            edittextTextWatcher(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText
            )
        )
        binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.addTextChangedListener(
            edittextTextWatcher(
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText
            )
        )
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
                bundle.putString(Constants.ITEM_QUANTITY, binding.quantityEditText.text.toString().trim())
                val intent = Intent(this, StockQuantityReconciliationActivity::class.java)
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
                    getString(R.string.error_item_part_code_message)
                )
            } else {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                )
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                )
            }
        })
        mViewModel.errorItemSerialNo.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                    getString(R.string.error_item_serial_no_message)
                )
            } else {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                )
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                )
            }
        })
        mViewModel.errorQuantity.observe(this, Observer {
            if (it) {
                enableErrorMessage(
                    binding.quantityEditTextLayout,
                    binding.quantityEditText,
                    getString(R.string.error_quantity_message)
                )
            } else {
                disableErrorMessage(
                    binding.quantityEditTextLayout,
                    binding.quantityEditText,
                )
            }
        })

        mViewModel.itemDto.observe(this, Observer { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        })

        mViewModel.isItemSerialized.observe(this, Observer { isItSerialized ->
//            binding.quantityEditText.isEnabled = !isItSerialized
        })

        mViewModel.quantityString.observe(this, Observer { value ->
            binding.quantityEditText.text = value.toEditable()
        })

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val data = it.extras
                    val serialItemsList =
                        data?.getParcelableArrayList(
                            Constants.CHECKED_SERIAL_ITEMS,
                            SerialItemsDto::class.java
                        )
                    mViewModel.setSelectedSerialItemsList(serialItemsList)
                }
            }
        }

    private fun clearFields() {
        binding.itemNameManufactureLayout.itemNameEnglishEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemNameArabicEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemStockEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemManufactureEditText.text =
            getString(R.string.empty).toEditable()
        binding.quantityEditText.text =
            getString(R.string.empty).toEditable()
        binding.locationSpinnerTV.text =
            getString(R.string.select_location).toEditable()

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }

}