package com.exert.wms.delivery.deliveryNote.item

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.alertDialog.AlertDialogDto
import com.exert.wms.alertDialog.AlertDialogWithCallBack
import com.exert.wms.databinding.ActivityDeliveryNoteItemBinding
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryNoteItemActivity :
    BaseActivity<DeliveryNoteItemViewModel, ActivityDeliveryNoteItemBinding>() {

    override val title = R.string.item_material_delivery

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_delivery_note_item

    override val mViewModel by lazy {
        getViewModel<DeliveryNoteItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var itemDto: DeliveryNoteItemsDetailsDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryNoteItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)

        observeViewModel()

        binding.quantityEditTextLayout.setEndIconOnClickListener {
            mViewModel.checkSerialItems()
        }

        binding.saveButton.setOnClickListener {
            mViewModel.saveItemStock(
                binding.quantityEditText.text.toString()
            )
        }
        binding.quantityEditText.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(text: Editable?) {
                mViewModel.setAdjustmentQuantity(text.toString())
            }

        })

        binding.quantityEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextViewVisibility(
                        binding.quantityHintTV,
                        View.GONE
                    )
                } else {
                    val value =
                        binding.quantityEditText.text.isNullOrEmpty()
                    setTextViewVisibility(
                        binding.quantityHintTV,
                        if (value) {
                            View.VISIBLE
                        } else View.GONE
                    )
                }
            }
    }

    private fun observeViewModel() {
        itemDto =
            intent.getSerializable(Constants.ITEM_DTO, DeliveryNoteItemsDetailsDto::class.java)
        mViewModel.setSelectedItemDto(itemDto)

        binding.itemNameManufactureLayout.itemStockLayout.visibility = View.GONE

        mViewModel.isLoadingData.observe(this) { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
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

        mViewModel.saveItemStatus.observe(this) {
            if (it) {
                showBriefToastMessage(
                    getString(R.string.item_saved_message),
                    coordinateLayout,
                    getColor(R.color.blue_50)
                )
                val intent = Intent().apply {
                    val bundle = Bundle()
                    bundle.putSerializable(
                        Constants.STOCK_ITEMS_DETAILS_DTO,
                        mViewModel.getSavedItemDto()
                    )
                    putExtras(bundle)
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
                val intent = Intent(this, DeliveryNoteQuantityActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.serial_items_empty_message),
                    coordinateLayout
                )
            }
        }

        mViewModel.itemDto.observe(this) { dto ->
            binding.itemDto = dto
            binding.itemNameManufactureLayout.itemStockEditText.setText(dto.Manufacturer)
            binding.executePendingBindings()
        }

        mViewModel.isItemSerialized.observe(this) { isItSerialized ->
            binding.quantityEditText.isEnabled = !isItSerialized
            binding.quantityEditTextLayout.isEndIconVisible = isItSerialized
            if (isItSerialized && binding.quantityEditText.text?.isNotEmpty() == true) {
                setTextViewVisibility(
                    binding.quantityHintTV,
                    View.GONE
                )
            } else if (!isItSerialized && binding.quantityEditText.text?.isEmpty() == true) {
                setTextViewVisibility(
                    binding.quantityHintTV,
                    View.VISIBLE
                )
            }
        }

        mViewModel.quantityString.observe(this) { value ->
            binding.quantityEditText.setText(value)
            setTextViewVisibility(
                binding.quantityHintTV,
                View.GONE
            )
        }

        mViewModel.returnedQuantityString.observe(this) { value ->
            binding.returnedQuantityEditText.setText(value)
        }

        mViewModel.errorReturningQty.observe(this) { show ->
            if (show) {
                showAlertDialog()
            }

        }
        mViewModel.returningQuantityString.observe(this) { value ->
            binding.quantityEditText.setText(value)
            setTextViewVisibility(
                binding.quantityHintTV,
                View.GONE
            )
        }

        mViewModel.enableSaveButton.observe(this) {
            binding.saveButton.isEnabled = it
        }
    }

    override fun onBindData(binding: ActivityDeliveryNoteItemBinding) {
        binding.viewModel = mViewModel
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val serialItemsList =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.getParcelableExtra(
                                Constants.CHECKED_SERIAL_ITEMS, SerialItemsDtoList::class.java
                            )
                        } else {
                            it.getParcelableExtra(Constants.CHECKED_SERIAL_ITEMS)
                        }

                    mViewModel.setSelectedSerialItemsList(serialItemsList)
                }
            }
        }

    private fun showAlertDialog() {
        val alertDialogDto = AlertDialogDto(
            title = getString(R.string.alert),
            message = getString(R.string.error_balance_returning_qty),
            positiveButtonText = getString(R.string.ok),
            showNegativeButton = false
        )
        AlertDialogWithCallBack.newInstance(alertDialogDto, onPositiveButtonCallBack = {})
            .show(this.supportFragmentManager, "AlertDialogWithCallBack")
    }
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }
}