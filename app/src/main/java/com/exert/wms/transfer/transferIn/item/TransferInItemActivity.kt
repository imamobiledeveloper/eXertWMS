package com.exert.wms.transfer.transferIn.item

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityTransferInItemBinding
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.transfer.api.ExternalTransferItemsDto
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransferInItemActivity :
    BaseActivity<TransferInItemViewModel, ActivityTransferInItemBinding>() {

    override val title = R.string.item_transfer_in

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_transfer_in_item

    override val mViewModel by lazy {
        getViewModel<TransferInItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var externalTransferItemsDto: ExternalTransferItemsDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferInItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()

        binding.quantityEditTextLayout.setEndIconOnClickListener {
            mViewModel.checkSerialItems()
        }
    }

    private fun observeViewModel() {
        externalTransferItemsDto =
            intent.getSerializable(Constants.ITEM_DTO, ExternalTransferItemsDto::class.java)
        mViewModel.setSelectedItemDto(externalTransferItemsDto)

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

        mViewModel.navigateToSerialNo.observe(this) {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                val intent = Intent(this, TransferInQuantityActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)

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
            binding.quantityEditText.isEnabled = false//!isItSerialized
//            binding.quantityEditTextLayout.isEndIconVisible = isItSerialized
        }

        mViewModel.quantityString.observe(this) { value ->
            binding.quantityEditText.setText(value)
        }
    }

    override fun onBindData(binding: ActivityTransferInItemBinding) {
        binding.viewModel = mViewModel
    }

}