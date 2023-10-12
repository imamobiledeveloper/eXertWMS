package com.exert.wms.returns.purchaseReturn.item

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
import com.exert.wms.databinding.ActivityPurchaseReturnItemBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.returns.api.PurchaseItemsDetailsDto
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class PurchaseReturnItemActivity :
    BaseActivity<PurchaseReturnItemViewModel, ActivityPurchaseReturnItemBinding>() {

    override val title = R.string.item_purchase_return

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_purchase_return_item

    override val mViewModel by lazy {
        getViewModel<PurchaseReturnItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var itemDto: PurchaseItemsDetailsDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseReturnItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)

        observeViewModel()

        binding.returningQuantityEditTextLayout.setEndIconOnClickListener {
            mViewModel.checkSerialItems()
        }

        binding.saveButton.setOnClickListener {
            mViewModel.saveItemStock(
                binding.returningQuantityEditText.text.toString()
            )
        }

        binding.returningQuantityEditText.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(text: Editable?) {
                mViewModel.setAdjustmentQuantity(text.toString())
            }

        })

        binding.returningQuantityEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setTextViewVisibility(
                        binding.returningQuantityHintTV,
                        View.GONE
                    )
                } else {
                    val value =
                        binding.returningQuantityEditText.text.isNullOrEmpty()
                    setTextViewVisibility(
                        binding.returningQuantityHintTV,
                        if (value) {
                            View.VISIBLE
                        } else View.GONE
                    )
                }
            }

    }

    private fun observeViewModel() {
        itemDto = intent.getSerializable(Constants.ITEM_DTO, PurchaseItemsDetailsDto::class.java)
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
                    msg, coordinateLayout
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
                val intent = Intent(this, PurchaseReturnQuantityActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)

            } else {
                showBriefToastMessage(getString(R.string.invalid_details_message), coordinateLayout)
            }
        }

        mViewModel.itemDto.observe(this) { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
            binding.itemNameManufactureLayout.itemManufactureEditText.setText(
                itemDto?.OrderedQty?.toString() ?: ""
            )
            binding.itemNameManufactureLayout.itemStockEditText.setText(dto.Manufacturer)
        }

        mViewModel.isItemSerialized.observe(this) { isItSerialized ->
            binding.returningQuantityEditText.isEnabled = !isItSerialized
            binding.returningQuantityEditTextLayout.isEndIconVisible = isItSerialized
            if (isItSerialized && binding.returningQuantityEditText.text?.isNotEmpty() == true) {
                setTextViewVisibility(
                    binding.returningQuantityHintTV,
                    View.GONE
                )
            } else if (!isItSerialized && binding.returningQuantityEditText.text?.isEmpty() == true) {
                setTextViewVisibility(
                    binding.returningQuantityHintTV,
                    View.VISIBLE
                )
            }
        }

        mViewModel.returningQuantityString.observe(this) { value ->
            binding.returningQuantityEditText.setText(value)
            setTextViewVisibility(
                binding.returningQuantityHintTV,
                View.GONE
            )
        }
        mViewModel.errorReturningQty.observe(this) { show ->
            if (show) {
                showAlertDialog()
            }

        }

        mViewModel.enableSaveButton.observe(this) {
            binding.saveButton.isEnabled = it
        }
    }

    private fun showAlertDialog() {
        val alertDialogDto = AlertDialogDto(
            title = getString(R.string.alert),
            message = getString(R.string.error_returning_qty),
            positiveButtonText = getString(R.string.ok),
            showNegativeButton = false
        )
        AlertDialogWithCallBack.newInstance(alertDialogDto, onPositiveButtonCallBack = {})
            .show(this.supportFragmentManager, "AlertDialogWithCallBack")
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

    override fun onBindData(binding: ActivityPurchaseReturnItemBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }
}