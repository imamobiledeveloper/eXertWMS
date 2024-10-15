package com.exert.wms.delivery.deliveryReceipt.item

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
import com.exert.wms.databinding.ActivityDeliveryReceiptItemBinding
import com.exert.wms.delivery.api.DeliveryReceiptItemsDetailsDto
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryReceiptItemActivity :
    BaseActivity<DeliveryReceiptItemViewModel, ActivityDeliveryReceiptItemBinding>() {

    override val title = R.string.item_goods_received

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_delivery_receipt_item

    override val mViewModel by lazy {
        getViewModel<DeliveryReceiptItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var itemDto: DeliveryReceiptItemsDetailsDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryReceiptItemBinding.inflate(layoutInflater)
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
    }

    private fun observeViewModel() {
        itemDto =
            intent.getSerializable(Constants.ITEM_DTO, DeliveryReceiptItemsDetailsDto::class.java)
        mViewModel.setSelectedItemDto(itemDto)

        binding.itemNameManufactureLayout.itemStockLayout.visibility = View.VISIBLE

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
                    getString(R.string.item_saved_message), coordinateLayout,
                    getColor(R.color.blue_50)
                )
                val intent = Intent().apply {
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.STOCK_ITEMS_DETAILS_DTO, mViewModel.getSavedItemDto())
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
                val intent = Intent(this, DeliveryReceiptQuantityActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)

            } else {
                showBriefToastMessage(getString(R.string.serial_items_empty_message), coordinateLayout)
            }
        }

        mViewModel.itemDto.observe(this) { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
            binding.itemNameManufactureLayout.itemManufactureEditText.setText(
                itemDto?.QTYOrdered?.toString() ?: "")
            binding.itemNameManufactureLayout.itemStockEditText.setText(dto.Manufacturer)
        }

        mViewModel.isItemSerialized.observe(this) { isItSerialized ->
            binding.quantityEditText.isEnabled = !isItSerialized
            binding.quantityEditTextLayout.isEndIconVisible = isItSerialized
        }

        mViewModel.quantityString.observe(this) { value ->
            binding.quantityEditText.setText(value)
        }

        mViewModel.errorQuantityType.observe(this) {
            if (it) {
                enableErrorMessage(
                    binding.quantityEditTextLayout,
                    binding.quantityEditText,
                    getString(R.string.quantity_empty_message), false
                )
            } else {
                disableErrorMessage(
                    binding.quantityEditTextLayout,
                    binding.quantityEditText,
                )
            }
        }
        mViewModel.enableSaveButton.observe(this) {
            binding.saveButton.isEnabled = it
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


    override fun onBindData(binding: ActivityDeliveryReceiptItemBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }
}