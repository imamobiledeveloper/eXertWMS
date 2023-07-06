package com.exert.wms.stockReconciliation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentStockReconciliationBinding
import com.exert.wms.mvvmbase.MVVMFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class StockReconciliationBaseFragment :
    MVVMFragment<StockReconciliationBaseViewModel, FragmentStockReconciliationBinding>() {

    override val title = R.string.stock_reconciliation

    override val mViewModel by lazy {
        getViewModel<StockReconciliationBaseViewModel>()
    }

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

    }

    private fun observeViewModel() {

    }


}