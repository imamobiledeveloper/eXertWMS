package com.exert.wms.delivery.deliveryNote.item

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.SerialItemsDto
import com.exert.wms.SerialItemsDtoList
import com.exert.wms.databinding.ActivityDeliveryNoteQuantityBinding
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto
import com.exert.wms.itemStocks.serialNumbers.SerialNumbersListAdapter
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.stockAdjustment.item.OnItemCheckListener
import com.exert.wms.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryNoteQuantityActivity :
    BaseActivity<DeliveryNoteItemViewModel, ActivityDeliveryNoteQuantityBinding>() {

    override val title = R.string.item_stock_status

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_delivery_note_quantity

    override val mViewModel by lazy {
        getViewModel<DeliveryNoteItemViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    var dnItemDto: DeliveryNoteItemsDetailsDto? = null
    var serialItemsList: SerialItemsDtoList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryNoteQuantityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        dnItemDto =
            intent.getSerializable(Constants.ITEM_DTO, DeliveryNoteItemsDetailsDto::class.java)
        mViewModel.setSelectedDeliveryNoteItemDto(dnItemDto)

        mViewModel.errorFieldMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        }

        mViewModel.convertedItemsDto.observe(this) { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
        }

        mViewModel.dnSerialItems.observe(this) { list ->
            if (list != null) {
                binding.serialNumbersListRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.serialNumbersListRecyclerView.adapter =
                    SerialNumbersListAdapter(
                        list,
                        checkBoxState = false,
                        object : OnItemCheckListener {
                            override fun onItemCheck(item: SerialItemsDto) {}
                            override fun onItemUncheck(item: SerialItemsDto) {}
                        })
            }
        }
    }

    override fun onBindData(binding: ActivityDeliveryNoteQuantityBinding) {
        binding.viewModel = mViewModel
        binding.executePendingBindings()
    }

}