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
//                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
//                bundle.putLong(Constants.ITEM_WAREHOUSE_ID, mViewModel.getSelectedWarehouseId())
//                bundle.putSerializable(Constants.WAREHOUSE, mViewModel.getWarehouseObject())
                val intent = Intent(requireContext(), SalesReturnItemActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.branch_empty_message),
                    coordinateLayout
                )
            }
        }
        mViewModel.warehouseStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setWarehouseList(it)
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