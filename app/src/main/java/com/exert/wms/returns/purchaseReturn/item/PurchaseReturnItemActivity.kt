package com.exert.wms.returns.purchaseReturn.item

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.alertDialog.AlertDialogDto
import com.exert.wms.alertDialog.AlertDialogWithCallBack
import com.exert.wms.databinding.ActivityPurchaseReturnItemBinding
import com.exert.wms.delivery.deliveryNote.item.DeliveryNoteQuantityActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.returns.salesReturn.item.SalesReturnQuantityActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
import com.exert.wms.warehouse.WarehouseDto
import com.google.android.material.textfield.TextInputEditText
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

    var warehouseDto: WarehouseDto? = null
    var warehouseId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseReturnItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)

        observeViewModel()

        binding.returningQuantityEditTextLayout.setEndIconOnClickListener {
            mViewModel.checkItemDetailsEntered(
                binding.returningQuantityEditText.text.toString()
            )
        }

    }

    private fun clearOtherField(
        clearEditText: TextInputEditText, clearHintTV: TextView
    ) {
        hideKeyBoard()
        clearFields()
        clearTextInputEditText(clearEditText, clearHintTV)
        mViewModel.clearPreviousSearchedListItems()
    }


    private fun observeViewModel() {
        warehouseId = intent.getLongExtra(Constants.ITEM_WAREHOUSE_ID, 0)
        warehouseDto = intent.getSerializable(Constants.WAREHOUSE, WarehouseDto::class.java)
//        mViewModel.setSelectedWarehouseDto(warehouseId, warehouseDto)

        binding.itemNameManufactureLayout.itemStockLabel.text = getString(R.string.item_part_code)
        binding.itemNameManufactureLayout.itemStockLabel.isEnabled = false

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
//                bundle.putSerializable(
//                    Constants.WAREHOUSE_STOCK_DETAILS, mViewModel.getWarehouseStockDetails()
//                )
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
        }

        mViewModel.isItemSerialized.observe(this) { isItSerialized ->
            binding.returningQuantityEditText.isEnabled = !isItSerialized
        }

        mViewModel.returningQuantityString.observe(this) { value ->
            binding.returningQuantityEditText.text = value.toEditable()
        }

        mViewModel.errorReturningQty.observe(this) { show ->
            if (show) {
                showAlertDialog()
            }
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

    private fun clearFields() {
        binding.itemNameManufactureLayout.itemNameEnglishEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemNameArabicEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemStockEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemManufactureEditText.text =
            getString(R.string.empty).toEditable()
        binding.returningQuantityEditText.text = getString(R.string.empty).toEditable()

    }

    override fun onBindData(binding: ActivityPurchaseReturnItemBinding) {
        binding.viewModel = mViewModel
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        super.onBackPressed()
    }
}