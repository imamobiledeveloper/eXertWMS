package com.exert.wms.itemStocks

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.exert.wms.R
import com.exert.wms.ScanBarcodeBroadcastListener
import com.exert.wms.ScanBarcodeDataReceiver
import com.exert.wms.databinding.FragmentItemStocksBinding
import com.exert.wms.itemStocks.warehouse.WarehouseListActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.utils.Constants
import com.exert.wms.utils.Constants.ACTION_BARCODE_DATA
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import com.exert.wms.utils.toEditable
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ItemStocksFragment : MVVMFragment<ItemStocksViewModel, FragmentItemStocksBinding>(),
    ScanBarcodeBroadcastListener {

    override val title = R.string.item_stocks

    override val showHomeButton: Int = 1

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

        addTextWatcherListeners()
        addFocusChangeListeners()
        observeViewModel()
        setItemPartCodeAndSerialNoCLickListeners()
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
            triggerScanner(requireActivity())
        }
        binding.itemPartCodeSerialNoLayout.scanItemSerialNoIV.setOnClickListener {
            clearOtherField(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                binding.itemPartCodeSerialNoLayout.itemPartCodeHintTV
            )
            mViewModel.setItemPartCodeValue(getString(R.string.empty))
            mViewModel.setIsItPartCodeScanRequest(false)
            triggerScanner(requireActivity())
        }
    }

    private fun clearOtherField(
        clearEditText: TextInputEditText,
        clearHintTV: TextView
    ) {
        hideKeyBoard()
        clearFields()
        clearTextInputEditText(clearEditText, clearHintTV)
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
    }

    private fun observeViewModel() {
        val sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            binding.itemNameManufactureLayout.itemStockEditText.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.edittext_disabled_rectangle_bg
                )
            );
        } else {
            binding.itemNameManufactureLayout.itemStockEditText.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.edittext_disabled_rectangle_bg
                )
        }

        binding.statusButton.setOnClickListener {
            mViewModel.checkItemStock()
        }
        binding.itemPartCodeSerialNoLayout.scanItemPartCodeIV.setOnClickListener {
            showBriefToastMessage(
                "Clicked Item Part code",
                coordinateLayout,
                requireActivity().getColor(R.color.blue_50)
            )
        }
        binding.itemPartCodeSerialNoLayout.scanItemSerialNoIV.setOnClickListener {
            showBriefToastMessage(
                "Clicked Item Serial number",
                coordinateLayout,
                requireActivity().getColor(R.color.blue_50)
            )
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
            clearFields()
        })

        mViewModel.errorItemPartCode.observe(viewLifecycleOwner, Observer {
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
        })
        mViewModel.errorItemSerialNo.observe(viewLifecycleOwner, Observer {
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
        })

        mViewModel.itemDto.observe(viewLifecycleOwner, Observer { dto ->
            binding.itemDto = dto
            binding.executePendingBindings()
            binding.itemNameManufactureLayout.itemStockEditText.isEnabled = dto.IsSerialItem == 1
        })

        mViewModel.itemBarCodeData.observe(viewLifecycleOwner, Observer { dto ->
            binding.itemBarCodeDto = dto
            binding.executePendingBindings()
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

    private fun clearFields() {
        binding.itemNameManufactureLayout.itemNameEnglishEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemNameArabicEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemStockEditText.text =
            getString(R.string.empty).toEditable()
        binding.itemNameManufactureLayout.itemManufactureEditText.text =
            getString(R.string.empty).toEditable()
    }

    override fun onBindData(binding: FragmentItemStocksBinding) {
        binding.viewModel = mViewModel
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(barcodeDataReceiver, IntentFilter(ACTION_BARCODE_DATA))
        claimScanner(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(barcodeDataReceiver)
        releaseScanner(requireActivity())
    }

    private val barcodeDataReceiver = ScanBarcodeDataReceiver(this)

    override fun onDataReceived(data: String?) {
        data?.let {
            mViewModel.setBarCodeData(it)
        }
    }

}