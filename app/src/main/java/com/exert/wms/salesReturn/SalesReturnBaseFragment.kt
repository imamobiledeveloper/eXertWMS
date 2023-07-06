package com.exert.wms.salesReturn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentSalesReturnBaseBinding
import com.exert.wms.mvvmbase.MVVMFragment
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

    }

    private fun observeViewModel() {

    }


}