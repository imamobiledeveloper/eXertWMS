package com.exert.wms.transfer.transferOut.item

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.*
import com.exert.wms.databinding.ActivityTransferOutItemBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
import com.exert.wms.warehouse.WarehouseDto
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransferOutItemActivity :
    BaseActivity<TransferOutItemViewModel, ActivityTransferOutItemBinding>(),
    ScanBarcodeBroadcastListener {

    override val title = R.string.item_transfer_out

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_transfer_out_item

    override val mViewModel by lazy {
        getViewModel<TransferOutItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var fromWarehouseDto: WarehouseDto? = null

    var fromWarehouseId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferOutItemBinding.inflate(layoutInflater)
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
        binding.itemPartCodeSerialNoLayout.scanItemPartCodeIV.setOnClickListener {
            clearOtherField(
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText,
                binding.itemPartCodeSerialNoLayout.itemSerialNoHintTV
            )
            mViewModel.setItemSerialNumberValue(getString(R.string.empty))
            mViewModel.searchItemWithPartCode()
            mViewModel.setIsItPartCodeScanRequest(true)
            triggerScanner(this)
        }
        binding.itemPartCodeSerialNoLayout.scanItemSerialNoIV.setOnClickListener {
            clearOtherField(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV
            )
            mViewModel.setItemPartCodeValue(getString(R.string.empty))
            mViewModel.setIsItPartCodeScanRequest(false)
            triggerScanner(this)
        }
    }

    private fun clearOtherField(
        clearEditText: TextInputEditText,
        clearHintTV: TextView
    ) {
        hideKeyBoard()
        clearFields()
        clearTextInputEditText(clearEditText, clearHintTV)
        mViewModel.clearPreviousSearchedListItems()
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
        binding.quantityEditText.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(text: Editable?) {
                mViewModel.setQuantity(text.toString())
            }

        })
    }

    private fun observeViewModel() {
        fromWarehouseId = intent.getLongExtra(Constants.ITEM_WAREHOUSE_ID, 0)
        fromWarehouseDto = intent.getSerializable(Constants.WAREHOUSE, WarehouseDto::class.java)
        mViewModel.setSelectedWarehouseDto(fromWarehouseId, fromWarehouseDto)

        binding.itemNameManufactureLayout.itemStockLayout.visibility = View.GONE
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

        mViewModel.errorItemSelectionMessage.observe(this) {
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
        }
        mViewModel.errorQuantityType.observe(this) {
            if (it) {
                enableErrorMessage(
                    binding.quantityEditTextLayout,
                    binding.quantityEditText,
                    getString(R.string.error_quantity_empty_message)
                )
            } else {
                disableErrorMessage(
                    binding.quantityEditTextLayout,
                    binding.quantityEditText,
                )
            }
        }

        mViewModel.saveItemStatus.observe(this) {
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
        }

        mViewModel.navigateToSerialNo.observe(this) {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                bundle.putParcelable(
                    Constants.USER_SELECTED_WAREHOUSE_LIST,
                    mViewModel.getUserSelectedSerialItemsList()
                )
                bundle.putSerializable(
                    Constants.WAREHOUSE_STOCK_DETAILS,
                    mViewModel.getWarehouseStockDetails()
                )
                val intent = Intent(this, TransferOutQuantityActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)

            } else {
                showBriefToastMessage(getString(R.string.invalid_details_message), coordinateLayout)
            }
        }
        mViewModel.enableSaveButton.observe(this) {
            binding.saveButton.isEnabled = it
        }

        mViewModel.errorItemPartCode.observe(this) {
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
        }
        mViewModel.errorItemSerialNo.observe(this) {
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
        }

        mViewModel.getItemsStatus.observe(this) {
            if (!it) {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        }

        mViewModel.errorGetItemsStatusMessage.observe(this) { status ->
            showBriefToastMessage(status, coordinateLayout)
        }

        mViewModel.itemDto.observe(this, Observer { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        })

        mViewModel.isItemSerialized.observe(this) { isItSerialized ->
            binding.quantityEditText.isEnabled = !isItSerialized
            binding.quantityEditTextLayout.isEndIconVisible = isItSerialized
        }

        mViewModel.quantityString.observe(this) { value ->
            binding.quantityEditText.text = value.toEditable()
        }

        mViewModel.itemBarCodeData.observe(this) { dto ->
            binding.itemBarCodeDto = dto
            binding.executePendingBindings()
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val serialItemsList =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.getParcelableExtra(
                                Constants.CHECKED_SERIAL_ITEMS,
                                SerialItemsDtoList::class.java
                            )
                        } else {
                            it.getParcelableExtra(Constants.CHECKED_SERIAL_ITEMS)
                        }

                    mViewModel.setSelectedSerialItemsList(serialItemsList)
                }
            }
        }

    private fun clearFields() {
        binding.itemNameManufactureLayout.itemNameEnglishEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemNameArabicEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemManufactureEditText.text =
            getString(R.string.empty).toEditable()
        binding.quantityEditText.text =
            getString(R.string.empty).toEditable()
        binding.saveButton.isEnabled = false

    }

    override fun onBindData(binding: ActivityTransferOutItemBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(barcodeDataReceiver, IntentFilter(Constants.ACTION_BARCODE_DATA))
        claimScanner(this)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(barcodeDataReceiver)
        releaseScanner(this)
    }

    private val barcodeDataReceiver = ScanBarcodeDataReceiver(this)

    override fun onDataReceived(data: String?) {
        data?.let {
            mViewModel.setBarCodeData(it)
        } ?: showBriefToastMessage(getString(R.string.error_barcode_message), coordinateLayout)
    }
}