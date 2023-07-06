package com.exert.wms.transferIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentTransferInBaseBinding
import com.exert.wms.mvvmbase.MVVMFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransferInBaseFragment :
    MVVMFragment<TransferInBaseViewModel, FragmentTransferInBaseBinding>() {

    override val title = R.string.transfer_in

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

    }

    private fun observeViewModel() {

    }


}