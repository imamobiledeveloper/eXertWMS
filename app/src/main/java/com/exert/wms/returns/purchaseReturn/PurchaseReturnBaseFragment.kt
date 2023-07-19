package com.exert.wms.returns.purchaseReturn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentPurchaseReturnBaseBinding
import com.exert.wms.mvvmbase.MVVMFragment
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

    }

    private fun observeViewModel() {

    }


}