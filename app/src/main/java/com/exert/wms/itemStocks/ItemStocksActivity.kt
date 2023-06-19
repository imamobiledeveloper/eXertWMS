package com.exert.wms.itemStocks

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityItemStocksBinding
import com.exert.wms.itemStocks.warehouse.WarehouseListActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ItemStocksActivity : BaseActivity<ItemStocksViewModel, ActivityItemStocksBinding>() {

    override val title = R.string.item_stocks

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_item_stocks

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
        binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout.setStartIconOnClickListener {
            showBriefToastMessage(
                "Clicked Item Part code",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
        }
        binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout.setStartIconOnClickListener {
            showBriefToastMessage(
                "Clicked Item Serial number",
                coordinateLayout,
                getColor(R.color.blue_50)
            )
        }
    }

    private fun observeViewModel() {
        binding.statusButton.setOnClickListener {
            mViewModel.checkItemStock()
        }
        binding.itemPartCodeSerialNoLayout.searchItemPartCodeIV.setOnClickListener {
            hideKeyBoard()
            mViewModel.searchItemWithPartCode(binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString())
        }
        binding.itemPartCodeSerialNoLayout.searchItemSerialNoIV.setOnClickListener {
            hideKeyBoard()
            mViewModel.searchItemWithSerialNumber(binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString())
        }
        mViewModel.isLoadingData.observe(this, Observer { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        })

        mViewModel.errorItemSelectionMessage.observe(this, Observer {
            if (it) {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                )
            } else {
                enableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                    getString(R.string.error_item_partcode_serial_no_empty_message)
                )
            }
        })

        mViewModel.itemStockStatus.observe(this, Observer {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                bundle.putString(
                    Constants.ITEM_PART_CODE,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString()
                )
                bundle.putString(
                    Constants.ITEM_SERIAL_NO,
                    binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString()
                )
                startActivity<WarehouseListActivity>(bundle)
            } else {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

        mViewModel.enableStatusButton.observe(this, Observer {
            binding.statusButton.isEnabled = it
        })

        mViewModel.errorGetItemsStatusMessage.observe(this, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
            binding.statusButton.isEnabled = false
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
            binding.itemNameManufactureLayout.itemStockEditText.isEnabled = dto.IsSerialItem == 1
        })

        mViewModel.errorFieldMessage.observe(this, Observer { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        })

    }

    override fun onBindData(binding: ActivityItemStocksBinding) {
        binding.viewModel = mViewModel
    }

}