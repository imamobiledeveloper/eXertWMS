package com.exert.wms.transfer.transferIn

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
import com.exert.wms.databinding.FragmentTransferInBaseBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.transfer.api.TransferInItemsDetailsDto
import com.exert.wms.transfer.transferIn.item.TransferInItemActivity
import com.exert.wms.transfer.transferIn.item.TransferInItemsListAdapter
import com.exert.wms.utils.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransferInBaseFragment :
    MVVMFragment<TransferInBaseViewModel, FragmentTransferInBaseBinding>() {

    override val title = R.string.transfer_in

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override val mViewModel by lazy {
        getViewModel<TransferInBaseViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransferInBaseBinding {
        return FragmentTransferInBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentTransferInBaseBinding) {
        binding.viewModel = mViewModel
    }

    private fun observeViewModel() {
        binding.fromWarehouseSpinner.selected { parent, position ->
            binding.fromWarehouseSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.fromWarehouseSpinnerTV.isActivated = true
            }
            mViewModel.selectedFromWarehouse(parent?.getItemAtPosition(position).toString())
            binding.fromWarehouseSpinner.setSelection(mViewModel.getSelectedFromWarehouseIndex())
        }
        binding.toWarehouseSpinner.selected { parent, position ->
            binding.toWarehouseSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.toWarehouseSpinnerTV.isActivated = true
            }
            mViewModel.selectedToWarehouse(parent?.getItemAtPosition(position).toString())
            binding.toWarehouseSpinner.setSelection(mViewModel.getSelectedToWarehouseIndex())
        }
        binding.transferOutNoSpinner.selected { parent, position ->
            binding.transferOutNoSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.transferOutNoSpinnerTV.isActivated = true
            }
            mViewModel.selectedTransferOutNo(parent?.getItemAtPosition(position).toString())
            binding.transferOutNoSpinner.setSelection(mViewModel.getSelectedToWarehouseIndex())
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
                    adapter = TransferInItemsListAdapter(list) {
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
                val intent = Intent(requireContext(), TransferInItemActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.warehouse_empty_message),
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
                    getString(R.string.success_save_stock_adjustment),
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
        binding.fromWarehouseSpinner.adapter = adapter
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Constants.STOCK_ITEMS_DETAILS_DTO,
                            TransferInItemsDetailsDto::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Constants.STOCK_ITEMS_DETAILS_DTO)
                    }
                    mViewModel.setTransferInItemDetails(item)
                }
            }
        }
}