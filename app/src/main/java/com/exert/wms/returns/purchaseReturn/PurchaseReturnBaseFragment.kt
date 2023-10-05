package com.exert.wms.returns.purchaseReturn

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
import com.exert.wms.databinding.FragmentPurchaseReturnBaseBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.returns.api.PurchaseItemsDetailsDto
import com.exert.wms.returns.purchaseReturn.item.PurchaseReturnItemActivity
import com.exert.wms.utils.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class PurchaseReturnBaseFragment :
    MVVMFragment<PurchaseReturnBaseViewModel, FragmentPurchaseReturnBaseBinding>() {

    override val title = R.string.purchase_return

    override val mViewModel by lazy {
        getViewModel<PurchaseReturnBaseViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPurchaseReturnBaseBinding {
        return FragmentPurchaseReturnBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentPurchaseReturnBaseBinding) {
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
        binding.vendorNameSpinner.selected { parent, position ->
            binding.vendorNameSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.vendorNameSpinnerTV.isActivated = true
            }
            mViewModel.selectedVendorName(parent?.getItemAtPosition(position).toString())
            binding.vendorNameSpinner.setSelection(mViewModel.getSelectedVendorNameIndex())
        }
        binding.purchaseInvoiceNoSpinner.selected { parent, position ->
            binding.purchaseInvoiceNoSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.purchaseInvoiceNoSpinnerTV.isActivated = true
            }
            mViewModel.selectedPurchaseInvoiceNo(parent?.getItemAtPosition(position).toString())
            binding.purchaseInvoiceNoSpinner.setSelection(mViewModel.getSelectedPurchaseInvoiceNoIndex())
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
                    adapter = PurchaseReturnListAdapter(list) {
                        navigateToPurchaseReturnItemScreen(it)
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
//                val intent = Intent(requireContext(), DeliveryNoteItemActivity::class.java)
//                intent.putExtras(bundle)
//                startForResult.launch(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.branch_empty_message),
                    coordinateLayout
                )
            }
        }

        mViewModel.saveItemStatus.observe(viewLifecycleOwner) {
            if (it) {
                showBriefToastMessage(
                    getString(R.string.success_save_purchase_return),
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
        mViewModel.vendorsStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setVendorsList(it)
            }
        }
        mViewModel.pInvoiceStringList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                setSalesOrdersList(it)
            }
        }
    }

    private fun navigateToPurchaseReturnItemScreen(itemDto: PurchaseItemsDetailsDto) {
        val bundle = Bundle()
//        bundle.putSerializable(Constants.ITEM_DTO, itemDto)
//        requireActivity().startActivity<DeliveryNoteItemActivity>(bundle)

        val intent = Intent(requireContext(), PurchaseReturnItemActivity::class.java)
        bundle.putSerializable(Constants.ITEM_DTO, itemDto)
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

    private fun setSalesOrdersList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(), stringList.toTypedArray(), android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.purchaseInvoiceNoSpinner.adapter = adapter
    }

    private fun setVendorsList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(),
            stringList.toTypedArray(),
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.vendorNameSpinner.adapter = adapter
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Constants.STOCK_ITEMS_DETAILS_DTO,
                            PurchaseItemsDetailsDto::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Constants.STOCK_ITEMS_DETAILS_DTO)
                    }
                    mViewModel.setPurchaseReturnItemsDetails(item)
                }
            }
        }
}