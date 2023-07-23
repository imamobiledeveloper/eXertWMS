package com.exert.wms.delivery.deliveryReceipt

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
import com.exert.wms.databinding.FragmentDeliveryReceiptBaseBinding
import com.exert.wms.delivery.api.DeliveryReceiptItemsDetailsDto
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.transfer.transferOut.item.TransferOutItemActivity
import com.exert.wms.utils.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeliveryReceiptBaseFragment :
    MVVMFragment<DeliveryReceiptBaseViewModel, FragmentDeliveryReceiptBaseBinding>() {

    override val title = R.string.delivery_receipt

    override val mViewModel by lazy {
        getViewModel<DeliveryReceiptBaseViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeliveryReceiptBaseBinding {
        return FragmentDeliveryReceiptBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentDeliveryReceiptBaseBinding) {
        binding.viewModel = mViewModel
    }

    private fun observeViewModel() {
        binding.purchaseOrderNoSpinner.selected { parent, position ->
            binding.purchaseOrderNoSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.purchaseOrderNoSpinnerTV.isActivated = true
            }
            mViewModel.selectedFromWarehouse(parent?.getItemAtPosition(position).toString())
            binding.purchaseOrderNoSpinner.setSelection(mViewModel.getSelectedFromWarehouseIndex())
        }
        binding.toWarehouseSpinner.selected { parent, position ->
            binding.toWarehouseSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.toWarehouseSpinnerTV.isActivated = true
            }
            mViewModel.selectedToWarehouse(parent?.getItemAtPosition(position).toString())
            binding.toWarehouseSpinner.setSelection(mViewModel.getSelectedToWarehouseIndex())
        }
        binding.addItemsTV.setOnClickListener {
            mViewModel.checkWarehouse()
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
                    adapter = DeliveryReceiptListAdapter(list) {
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
        mViewModel.errorWarehouse.observe(viewLifecycleOwner) {
            if (it) {
                val bundle = Bundle()
//                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
//                bundle.putLong(Constants.ITEM_WAREHOUSE_ID, mViewModel.getSelectedWarehouseId())
//                bundle.putSerializable(Constants.WAREHOUSE, mViewModel.getWarehouseObject())
                val intent = Intent(requireContext(), TransferOutItemActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.warehouse_from_to_empty_message),
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
                    getString(R.string.success_save_delivery_receipt),
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
        binding.toWarehouseSpinner.adapter = adapter
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Constants.STOCK_ITEMS_DETAILS_DTO,
                            DeliveryReceiptItemsDetailsDto::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Constants.STOCK_ITEMS_DETAILS_DTO)
                    }
                    mViewModel.setDeliveryReceiptItemsDetailsDto(item)
                }
            }
        }
}