package com.exert.wms.stockReconciliation

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.R
import com.exert.wms.databinding.FragmentStockReconciliationBinding
import com.exert.wms.home.HomeActivity
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.stockReconciliation.api.ReconciliationItemsDetailsDto
import com.exert.wms.stockReconciliation.item.StockItemReconciliationActivity
import com.exert.wms.utils.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockReconciliationBaseFragment :
    MVVMFragment<StockReconciliationBaseViewModel, FragmentStockReconciliationBinding>() {

    override val title = R.string.stock_reconciliation

    override val mViewModel by lazy {
        getViewModel<StockReconciliationBaseViewModel>()
    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStockReconciliationBinding {
        return FragmentStockReconciliationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentStockReconciliationBinding) {
        binding.viewModel = mViewModel
    }

    private fun observeViewModel() {
        binding.warehouseSpinner.selected { parent, position ->
            binding.warehouseSpinnerTV.text = parent?.getItemAtPosition(position).toString()
            if (position != 0) {
                binding.warehouseSpinnerTV.isActivated = true
            }
            mViewModel.selectedWarehouse(parent?.getItemAtPosition(position).toString())
            binding.warehouseSpinner.setSelection(mViewModel.getSelectedWarehouseIndex())
        }

        binding.addItemsTV.setOnClickListener {
            mViewModel.checkWarehouse()
        }

        binding.updateButton.setOnClickListener {
            mViewModel.saveItems()
        }
        mViewModel.isLoadingData.observe(viewLifecycleOwner, Observer { status ->
            if (status) {
                binding.progressBar.show()
            } else {
                binding.progressBar.hide()
            }
        })

        mViewModel.enableUpdateButton.observe(viewLifecycleOwner, Observer {
            binding.updateButton.isEnabled = it
        })

        mViewModel.warehouseStringList.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                setWarehouseList(it)
            }
        })

        mViewModel.itemsList.observe(viewLifecycleOwner, Observer { list ->
            if (list != null && list.isNotEmpty()) {
                binding.itemsListRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.itemsListRecyclerView.apply {
                    adapter = StockReconciliationItemsListAdapter(list) {
//                        navigateToItemAdjustment(it)
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
        })
        mViewModel.errorWarehouse.observe(viewLifecycleOwner, Observer {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(Constants.ITEM_DTO, mViewModel.getItemDto())
                bundle.putSerializable(Constants.WAREHOUSE, mViewModel.getWarehouseObject())
                val intent = Intent(requireContext(), StockItemReconciliationActivity::class.java)
                intent.putExtras(bundle)
                startForResult.launch(intent)
            } else {
                showBriefToastMessage(
                    getString(R.string.warehouse_empty_message),
                    coordinateLayout
                )
            }
        })
        mViewModel.errorFieldMessage.observe(viewLifecycleOwner, Observer { msg ->
            if (msg.isNotEmpty()) {
                showBriefToastMessage(
                    msg,
                    coordinateLayout
                )
            }
        })

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
    }

    private fun setWarehouseList(stringList: List<String>) {
        val adapter = SpinnerCustomAdapter(
            requireContext(),
            stringList.toTypedArray(),
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        binding.warehouseSpinner.adapter = adapter
    }

    private fun navigateToItemAdjustment(itemName: String) {
        activity?.startActivity<StockItemReconciliationActivity>()
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(
                            Constants.STOCK_ITEMS_DETAILS_DTO,
                            ReconciliationItemsDetailsDto::class.java
                        )
                    } else {
                        intent.getParcelableExtra(Constants.STOCK_ITEMS_DETAILS_DTO)
                    }
                    mViewModel.setStockItemDetails(item)
                }
            }
        }
}