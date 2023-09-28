package com.exert.wms.delivery.deliveryNote

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.R
import com.exert.wms.databinding.FragmentDeliveryNoteBaseBinding
import com.exert.wms.delivery.api.DeliveryNoteItemsDetailsDto
import com.exert.wms.delivery.deliveryNote.item.DeliveryNoteItemActivity
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.utils.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryNoteBaseFragment :
    MVVMFragment<DeliveryNoteBaseViewModel, FragmentDeliveryNoteBaseBinding>() {

    override val title = R.string.delivery_note

    override val mViewModel by lazy {
        getViewModel<DeliveryNoteBaseViewModel>()
    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentDeliveryNoteBaseBinding {
        return FragmentDeliveryNoteBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentDeliveryNoteBaseBinding) {
        binding.viewModel = mViewModel
    }

    private fun observeViewModel() {
        binding.branchSpinner.selected { parent, position ->
            binding.branchSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.branchSpinnerTV.isActivated = true
            }
            mViewModel.selectedBranch(parent?.getItemAtPosition(position).toString())
            binding.branchSpinner.setSelection(mViewModel.getSelectedBranchIndex())
        }
        binding.customerNameSpinner.selected { parent, position ->
            binding.customerNameSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.customerNameSpinnerTV.isActivated = true
            }
            mViewModel.selectedCustomerName(parent?.getItemAtPosition(position).toString())
            binding.customerNameSpinner.setSelection(mViewModel.getSelectedCustomerNameIndex())
        }
        binding.salesInvoiceNoSpinner.selected { parent, position ->
            binding.salesInvoiceNoSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.salesInvoiceNoSpinnerTV.isActivated = true
            }
            mViewModel.selectedSalesInvoiceNo(parent?.getItemAtPosition(position).toString())
            binding.salesInvoiceNoSpinner.setSelection(mViewModel.getSelectedSalesInvoiceNoIndex())
        }

        binding.updateButton.setOnClickListener {
            mViewModel.saveItems()
        }
        mViewModel.isLoadingData.observe(viewLifecycleOwner) { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        }

        mViewModel.enableUpdateButton.observe(viewLifecycleOwner) {
            binding.updateButton.isEnabled = it
        }
        mViewModel.itemsList.observe(viewLifecycleOwner) { list ->
            if (list != null && list.isNotEmpty()) {
                binding.itemsListRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.itemsListRecyclerView.apply {
                    adapter = DeliveryNoteListAdapter(list) {
                        navigateToDeliveryNoteItemScreen(it)
                    }
                    val dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)
                    dividerDrawable?.let {
                        val dividerItemDecoration: RecyclerView.ItemDecoration =
                            DividerItemDecorator(it)
                        addItemDecoration(dividerItemDecoration)
                    }

                    show()
                }
            } else {
                binding.itemsListRecyclerView.hide()
            }
        }
        mViewModel.errorBranch.observe(viewLifecycleOwner) {
            if (it) {
                val bundle = Bundle()
                val intent = Intent(requireContext(), DeliveryNoteItemActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.branch_empty_message), coordinateLayout
                )
            }
        }
        mViewModel.branchesStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setBranchesList(it)
            }
        }
        mViewModel.customersStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setVendorsList(it)
            }
        }
        mViewModel.salesOrdersStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setSalesOrdersList(it)
            }
        }

        mViewModel.saveItemStatus.observe(viewLifecycleOwner) {
            if (it) {
                showBriefToastMessage(
                    getString(R.string.success_save_delivery_note),
                    coordinateLayout,
                    bgColor = requireActivity().getColor(R.color.green_msg)
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    (activity as HomeActivity).setSelectedItem(0)
                }, 2000)
            }
        }
        mViewModel.errorFieldMessage.observe(viewLifecycleOwner) { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg, coordinateLayout
                )
            }
        }
    }

    private fun setSalesOrdersList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(), stringList.toTypedArray(), android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.salesInvoiceNoSpinner.adapter = adapter
    }

    private fun setVendorsList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(), stringList.toTypedArray(), android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.customerNameSpinner.adapter = adapter
    }

    private fun setBranchesList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(), stringList.toTypedArray(), android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.branchSpinner.adapter = adapter
    }

    private fun navigateToDeliveryNoteItemScreen(itemsDto: DeliveryNoteItemsDetailsDto) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.ITEM_DTO, itemsDto)
        requireActivity().startActivity<DeliveryNoteItemActivity>(bundle)
    }
}