package com.exert.wms.transferOut

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exert.wms.R
import com.exert.wms.databinding.FragmentTransferOutBaseBinding
import com.exert.wms.mvvmbase.MVVMFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel

class TransferOutBaseFragment :
    MVVMFragment<TransferOutBaseViewModel, FragmentTransferOutBaseBinding>() {

    override val title = R.string.transfer_out

    override val mViewModel by lazy {
        getViewModel<TransferOutBaseViewModel>()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransferOutBaseBinding {
        return FragmentTransferOutBaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    override fun onBindData(binding: FragmentTransferOutBaseBinding) {

    }

    private fun observeViewModel() {

    }


}