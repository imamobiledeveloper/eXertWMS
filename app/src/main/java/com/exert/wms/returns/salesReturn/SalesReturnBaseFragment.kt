package com.exert.wms.returns.salesReturn

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.R
import com.exert.wms.databinding.FragmentSalesReturnBaseBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.returns.api.SalesItemsDetailsDto
import com.exert.wms.returns.salesReturn.item.SalesReturnItemActivity
import com.exert.wms.utils.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class SalesReturnBaseFragment :
    MVVMFragment<SalesReturnBaseViewModel, FragmentSalesReturnBaseBinding>() {

    override val title = R.string.sales_return

    override val mViewModel by lazy {
        getViewModel<SalesReturnBaseViewModel>()
    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSalesReturnBaseBinding {
        return FragmentSalesReturnBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentSalesReturnBaseBinding) {
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
                binding.itemsListRecyclerView.show()
                binding.itemsListRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.itemsListRecyclerView.apply {
                    adapter = SalesReturnListAdapter(list) {
                        navigateToSalesReturnItemScreen(it)
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

        mViewModel.saveItemStatus.observe(viewLifecycleOwner) {
            if (it) {
                showBriefToastMessage(
                    getString(R.string.success_save_sales_return),
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
                    msg,
                    coordinateLayout
                )
            }
        }
        mViewModel.branchesStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setWarehouseList(it)
            }
        }
        mViewModel.customersStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setCustomersList(it)
            }
        }
        mViewModel.pInvoiceStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setPurchaseInvoicesList(it)
            }
        }
    }

    private fun navigateToSalesReturnItemScreen(itemsDto: SalesItemsDetailsDto) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.ITEM_DTO, itemsDto)
        val intent = Intent(requireContext(), SalesReturnItemActivity::class.java)
        intent.putExtras(bundle)
        startForResult.launch(intent)
    }

    private fun setWarehouseList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(),
            stringList.toTypedArray(),
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.branchSpinner.adapter = adapter
    }

    private fun setPurchaseInvoicesList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(), stringList.toTypedArray(), android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.salesInvoiceNoSpinner.adapter = adapter
    }

    private fun setCustomersList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(),
            stringList.toTypedArray(),
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.customerNameSpinner.adapter = adapter
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Constants.STOCK_ITEMS_DETAILS_DTO,
                            SalesItemsDetailsDto::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Constants.STOCK_ITEMS_DETAILS_DTO)
                    }
                    mViewModel.setSalesItemsDetails(item)
                }
            }
        }
}