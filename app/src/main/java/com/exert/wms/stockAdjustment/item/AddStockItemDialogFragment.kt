package com.exert.wms.stockAdjustment.item

import android.app.DatePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.exert.wms.R
import com.exert.wms.databinding.FragmentAddStockItemDialogLayoutBinding
import com.exert.wms.mvvmbase.MVVMBottomSheetDialogFragment
import com.exert.wms.utils.SpinnerCustomAdapter
import com.exert.wms.utils.toEditable
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.*

class AddStockItemDialogFragment(listener: OnItemAddListener) :
    MVVMBottomSheetDialogFragment<AddStockItemViewModel, FragmentAddStockItemDialogLayoutBinding>() {

    override val mViewModel by lazy {
        getViewModel<AddStockItemViewModel>()
    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    private var onAddItemListener: OnItemAddListener? = null

    init {
        onAddItemListener = listener
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddStockItemDialogLayoutBinding {
        return FragmentAddStockItemDialogLayoutBinding.inflate(inflater, container, false)
    }

    override fun onBindData(binding: FragmentAddStockItemDialogLayoutBinding) {
        val cal = Calendar.getInstance()
        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        var day = cal.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, yearOfDate, monthOfYear, dayOfMonth ->
                binding.manufactureDateEditText.text =
                    "$dayOfMonth/${mViewModel.getMonthIn2Digits(monthOfYear)}/${yearOfDate}".toEditable()
                mViewModel.setSelectedDate(yearOfDate, monthOfYear, dayOfMonth)
            }
        val mDay = cal.get(Calendar.DATE)
        cal.set(Calendar.DAY_OF_MONTH, mDay - 1)

        binding.manufactureDateEditText.setOnClickListener {
            if (mViewModel.getSelectedYear() > 0 && mViewModel.getSelectedMonth() > 0 && mViewModel.getSelectedDayOfMonth() > 0) {
                year = mViewModel.getSelectedYear()
                month = mViewModel.getSelectedMonth()
                day = mViewModel.getSelectedDayOfMonth()
            }
            val datePicker = DatePickerDialog(
                requireContext(), dateSetListener,
                year,
                month,
                day
            )

            datePicker.datePicker.maxDate = cal.timeInMillis

            datePicker.show()
        }

        binding.warrantyPeriodSpinner.let { spinner ->
            setWarrantyList(spinner)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    binding.warrantyPeriodSpinnerTV.text =
                        parent?.getItemAtPosition(position).toString()
                    if (position != 0) {
                        binding.warrantyPeriodSpinnerTV.isActivated = true
                    }
                    mViewModel.setWarrantyPeriod(parent?.getItemAtPosition(position).toString())
                    spinner.setSelection(mViewModel.getSelectedWarrantyPeriodIndex())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.addStockItemButton.setOnClickListener {
            mViewModel.addItemDetails(
                binding.manufactureDateEditText.text.toString(),
                binding.warrantyPeriodSpinnerTV.text.toString(),
                binding.serialNoEditText.text.toString()
            )
        }

        mViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        }

        mViewModel.serialItem.observe(viewLifecycleOwner) { item ->
            onAddItemListener?.let {
                it.onAddItem(item)
            }
            dismiss()
        }
    }

    private fun setWarrantyList(warrantyPeriodSpinner: Spinner) {
        val yearsList = resources.getStringArray(R.array.warranty_period_list)
        val warrantyList = yearsList.toMutableList()
        warrantyList.add(0, getString(R.string.select_warranty_period))

        mViewModel.setWarrantyPeriodList(warrantyList)
        val adapter = SpinnerCustomAdapter(
            requireContext(),
            warrantyList.toTypedArray(),
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        warrantyPeriodSpinner.adapter = adapter
    }
}