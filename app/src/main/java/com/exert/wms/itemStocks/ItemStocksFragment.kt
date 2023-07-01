package com.exert.wms.itemStocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.R
import com.exert.wms.databinding.FragmentItemStocksBinding
import com.exert.wms.itemStocks.warehouse.WarehouseListActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.utils.Constants
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ItemStocksFragment : MVVMFragment<ItemStocksViewModel, FragmentItemStocksBinding>() {

    override val title = R.string.item_stocks

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentItemStocksBinding {
        return FragmentItemStocksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout.setStartIconOnClickListener {
            showBriefToastMessage(
                "Clicked Item Part code",
                coordinateLayout,
                requireActivity().getColor(R.color.blue_50)
            )
        }
        binding.itemPartCodeSerialNoLayout.itemSerialNoEditTextLayout.setStartIconOnClickListener {
            showBriefToastMessage(
                "Clicked Item Serial number",
                coordinateLayout,
                requireActivity().getColor(R.color.blue_50)
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
        mViewModel.isLoadingData.observe(viewLifecycleOwner, Observer { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        })

        mViewModel.errorItemSelectionMessage.observe(viewLifecycleOwner, Observer {
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

        mViewModel.itemStockStatus.observe(viewLifecycleOwner, Observer {
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
                activity?.startActivity<WarehouseListActivity>(bundle)
            } else {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

        mViewModel.enableStatusButton.observe(viewLifecycleOwner, Observer {
            binding.statusButton.isEnabled = it
        })

        mViewModel.errorGetItemsStatusMessage.observe(viewLifecycleOwner, Observer { status ->
            showBriefToastMessage(status, coordinateLayout)
            binding.statusButton.isEnabled = false
        })

        mViewModel.errorItemPartCode.observe(viewLifecycleOwner, Observer {
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
        mViewModel.errorItemSerialNo.observe(viewLifecycleOwner, Observer {
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

        mViewModel.itemDto.observe(viewLifecycleOwner, Observer { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
            binding.itemNameManufactureLayout.itemStockEditText.isEnabled = dto.IsSerialItem == 1
        })

        mViewModel.errorFieldMessage.observe(viewLifecycleOwner, Observer { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        })
    }

    override fun onBindData(binding: FragmentItemStocksBinding) {
        binding.viewModel = mViewModel
    }

}