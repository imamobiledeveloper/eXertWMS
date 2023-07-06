package com.exert.wms.deliveryReceipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentDeliveryReceiptBaseBinding
import com.exert.wms.databinding.FragmentStockReconciliationBinding
import com.exert.wms.mvvmbase.MVVMFragment
import com.exert.wms.stockReconciliation.StockReconciliationBaseViewModel
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

    }

    private fun observeViewModel() {

    }


}