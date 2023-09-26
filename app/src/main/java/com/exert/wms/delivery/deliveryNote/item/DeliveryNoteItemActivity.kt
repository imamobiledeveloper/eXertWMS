package com.exert.wms.delivery.deliveryNote.item

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityDeliveryNoteItemBinding
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryNoteItemActivity :
    BaseActivity<DeliveryNoteItemViewModel, ActivityDeliveryNoteItemBinding>() {

    override val title = R.string.item_delivery_note

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_transfer_in_item

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

        mViewModel.navigateToSerialNo.observe(this) {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                val intent = Intent(this, DeliveryNoteQuantityActivity::class.java)
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
            binding.quantityEditText.text = value.toEditable()
        }
    }

    override fun onBindData(binding: ActivityDeliveryNoteItemBinding) {
        binding.viewModel = mViewModel
    }

}